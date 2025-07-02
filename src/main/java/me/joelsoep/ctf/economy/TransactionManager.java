package me.joelsoep.ctf.economy;

import com.eduardomcb.discord.webhook.WebhookClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import lombok.Getter;
import lombok.extern.java.Log;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.discord.DiscordWebhook;
import me.joelsoep.ctf.economy.event.TransactionCancellationEvent;
import me.joelsoep.ctf.economy.event.TransactionCompleteEvent;
import me.joelsoep.ctf.economy.event.TransactionCreateEvent;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.game.task.CallEventTask;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.OfflineCTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.store.StoreItem;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * class for async use only. Using this on the main thread may cause blocks.
 */
public class TransactionManager {

    private static final String SERVER_ID = LocalConfig.PROXY_MODE_ID != null ? LocalConfig.PROXY_MODE_ID : "NO_ID_FOUND";
    private static final long LOCK_TIMEOUT_MS = 5 * 60 * 1000; // 5 minutes

    public static Transaction createTransaction(UUID userId, StoreItem item, String reason) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        int amount = (int) Math.round(item.getPrice() * item.getDiscountCoefficient());
        Transaction transaction = new Transaction(userId, item.getPrefix() + item.getName(), amount, reason);
        LogModule.CORE.log("Creating new transaction with uuid %s".formatted(transaction.getUuid()));

        TransactionCreateEvent event = new TransactionCreateEvent(transaction);
        Bukkit.getScheduler().runTask(Main.getInstance(), new CallEventTask(event));

        // Persist immediately as pending and locked
        Document document = new Document("_id", transaction.getUuid().toString())
                .append("user_id", transaction.getUserId().toString())
                .append("item_name", transaction.getItemName())
                .append("amount", transaction.getAmount())
                .append("deducted", transaction.isDeducted())
                .append("reason", transaction.getReason())
                .append("state", TransactionState.PENDING.toString())
                .append("locked_by", SERVER_ID)
                .append("lock_time", System.currentTimeMillis())
                .append("date", transaction.getDate());

        MongoManager.insertOne(MongoManager.getCollection("transaction"), document, new IFindOneSubscriber<>() {
            @Override
            public void onResult(InsertOneResult result) {
                if (!result.wasAcknowledged()) {
                    cancelTransaction(transaction, "MongoDB pushing failed.");
                    return;
                }

                if (!deduct(transaction)) {
                    cancelTransaction(transaction, "Payment deduction failed.");
                    future.complete(false);
                    return;
                } else {
                    transaction.setDeducted(true);
                    MongoManager.getCollection("transaction").updateOne(Filters.eq("_id", transaction.getUuid().toString()), Updates.set("deducted", true))
                            .subscribe(new Subscriber<UpdateResult>() {
                                @Override
                                public void onSubscribe(Subscription s) {
                                    s.request(1);
                                }

                                @Override
                                public void onNext(UpdateResult updateResult) {
                                    if (!updateResult.wasAcknowledged()) {
                                        cancelTransaction(transaction, "Failed to update deduction state in database");
                                    } else {
                                        completeTransaction(transaction);
                                    }
                                }

                                @Override
                                public void onError(Throwable t) {
                                    cancelTransaction(transaction, "An error occurred while updating deduction state.");
                                    t.printStackTrace();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }

                postWebhook(transaction, null);

            }

            @Override
            public void onError(Throwable throwable) {
                LogModule.ECONOMY.warning("Failed to insert transaction into Mongo.");
                cancelTransaction(transaction, "An error occurred while attempting to communicate with MongoDB.");
                throwable.printStackTrace();
                future.complete(false);
            }
        });
        return transaction;
    }

    public static void resumePendingTransactions() {
        attemptPendingTransactions();
    }

    public static void attemptPendingTransactions() {
        long now = System.currentTimeMillis();

        Bson filter = Filters.and(
                Filters.eq("state", "PENDING"),
                Filters.or(
                        Filters.eq("locked_by", null),
                        Filters.lt("lock_time", now - LOCK_TIMEOUT_MS)
                )
        );

        Bson update = Updates.combine(
                Updates.set("locked_by", SERVER_ID),
                Updates.set("lock_time", now)
        );

        FindOneAndUpdateOptions opts = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

        MongoManager.getCollection("transaction")
                .findOneAndUpdate(filter, update, opts)
                .subscribe(new Subscriber<Document>() {
                    private Document document;

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                    }

                    @Override
                    public void onNext(Document document) {
                        this.document = document;
                    }

                    @Override
                    public void onError(Throwable t) {
                        LogModule.ECONOMY.warning("Error resuming transaction: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (document == null) {
                            return;
                        }
                        Transaction transaction = Transaction.fromDocument(document);

                        // Try to reprocess
                        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                            LogModule.ECONOMY.log("Resuming transaction %s".formatted(transaction.getUuid()));

                            if (!transaction.isDeducted()) {
                            if (!deduct(transaction)) {
                                cancelTransaction(transaction, "Failed to deduct coins on resume.");
                                return;
                            } else {
                                pushDeduction(transaction);
                            }
                            }

                            postWebhook(transaction, null).thenAccept(success -> {
                                if (!success) {
                                    cancelTransaction(transaction, "Webhook failed on resume.");
                                } else {
                                    completeTransaction(transaction);
                                }
                                resumePendingTransactions();
                            });
                        });

                    }
                });
    }

    public static void pushDeduction(Transaction transaction) {
        transaction.setDeducted(true);
        MongoManager.getCollection("transaction").updateOne(Filters.eq("_id", transaction.getUuid().toString()), Updates.set("deducted", true))
                .subscribe(new Subscriber<UpdateResult>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                    }

                    @Override
                    public void onNext(UpdateResult updateResult) {
                        if (!updateResult.wasAcknowledged()) {
                            cancelTransaction(transaction, "Failed to update deduction state in database");
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        cancelTransaction(transaction, "An error occurred while updating deduction state.");
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static CompletableFuture<Boolean> cancelTransaction(Transaction transaction, String reason) {
        transaction.setState(TransactionState.CANCELLED);
        MongoManager.getCollection("transaction").updateOne(
                Filters.eq("_id", transaction.getUuid().toString()),
                Updates.combine(
                        Updates.set("state", TransactionState.CANCELLED.toString()),
                        Updates.set("locked_by", null),
                        Updates.set("cancel_reason", reason)
                )
        ).subscribe(new Subscriber<UpdateResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(UpdateResult updateResult) {
                if (!updateResult.wasAcknowledged()) {
                    LogModule.ECONOMY.log("Could not post transaction cancel state for transaction %s.".formatted(transaction.getUuid().toString()));
                    postWebhook(transaction, "WARNING: THIS CANCELLATION HAS NOT BEEN SAVED TO THE DATABASE!!!!!");
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

        Bukkit.getScheduler().runTask(Main.getInstance(),
                new CallEventTask(new TransactionCancellationEvent(transaction, reason)));

        if (transaction.isDeducted()) {
            refund(transaction);
        }

        postWebhook(transaction, reason);
        return CompletableFuture.completedFuture(true);
    }

    public static void completeTransaction(Transaction transaction) {
        try {
            LogModule.ECONOMY.log("COMPLETING RN");
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            MongoManager.getCollection("transaction").updateOne(
                    Filters.eq("_id", transaction.getUuid().toString()),
                    Updates.combine(
                            Updates.set("state", TransactionState.COMPLETED.toString()),
                            Updates.set("locked_by", null)
                    )
            ).subscribe(new Subscriber<UpdateResult>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(1);
                }

                @Override
                public void onNext(UpdateResult updateResult) {
                    if (!updateResult.wasAcknowledged()) {
                        LogModule.ECONOMY.log("Something went wrong while marking transaction %s as completed in db (not acknowledged).".formatted(transaction.getUuid().toString()));
                        cancelTransaction(transaction, "Failed to push completion state (write not acknowledged).");
                        future.complete(false);
                    } else {
                        future.complete(true);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    LogModule.ECONOMY.log("Something went wrong while marking transaction %s as completed in db.".formatted(transaction.getUuid().toString()));
                    cancelTransaction(transaction, "Failed to push completion state.");
                    future.complete(false);
                }

                @Override
                public void onComplete() {

                }
            });

            future.thenAcceptAsync(bool -> {
                if (bool) {
                    FindPublisher<Document> publisher = MongoManager.getCollection("player").find(Filters.eq("_id", transaction.getUserId().toString()));
                    publisher.subscribe(new Subscriber<Document>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            s.request(1);
                            LogModule.ECONOMY.log("subbing rn");
                        }

                        @Override
                        public void onNext(Document document) {
                            List<Object> list = document.getList("ownedCosmetics", Object.class);
                            if (list == null) {
                                MongoManager.getCollection("player").updateOne(Filters.eq("_id", transaction.getUserId().toString()), Updates.set("ownedCosmetics", List.of(transaction.getItemName())))
                                        .subscribe(new Subscriber<UpdateResult>() {
                                            @Override
                                            public void onSubscribe(Subscription s) {
                                                s.request(1);
                                            }

                                            @Override
                                            public void onNext(UpdateResult updateResult) {
                                                if (!updateResult.wasAcknowledged()) {
                                                    cancelTransaction(transaction, "failed to insert ownedCosmetics field.");
                                                }
                                                transaction.setState(TransactionState.COMPLETED);
                                                Bukkit.getScheduler().runTask(Main.getInstance(),
                                                        new CallEventTask(new TransactionCompleteEvent(transaction)));
                                            }

                                            @Override
                                            public void onError(Throwable t) {
                                                cancelTransaction(transaction, "An error occurred while writing to MongoDB");
                                                t.printStackTrace();
                                            }

                                            @Override
                                            public void onComplete() {
                                            }
                                        });
                            } else {
                                MongoManager.getCollection("player").updateOne(
                                        Filters.eq("_id", transaction.getUserId().toString()),
                                        Updates.addToSet("ownedCosmetics", transaction.getItemName())
                                ).subscribe(new Subscriber<UpdateResult>() {
                                    @Override
                                    public void onSubscribe(Subscription s) {
                                        s.request(1);
                                    }

                                    @Override
                                    public void onNext(UpdateResult updateResult) {
                                        if (!updateResult.wasAcknowledged()) {
                                            cancelTransaction(transaction, "Failed to save data cosmetic to database.");
                                        } else {
                                            transaction.setState(TransactionState.COMPLETED);
                                            postWebhook(transaction, null);
                                            Bukkit.getScheduler().runTask(Main.getInstance(),
                                                    new CallEventTask(new TransactionCompleteEvent(transaction)));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable t) {
                                        cancelTransaction(transaction, "An error occurred while trying to save cosmetic to database.");
                                        t.printStackTrace();
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            cancelTransaction(transaction, "An error occurred whilst writing an update to MongoDB");
                            t.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
            });

            // TODO: Actually give the user the item
        } catch (Exception e) {
            cancelTransaction(transaction, "An error occurred while executing completeTransaction.");
            e.printStackTrace();
        }
    }

    private static CompletableFuture<Boolean> postWebhook(Transaction transaction, @Nullable String cancelReason) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        WebhookClient.Callback callback = new WebhookClient.Callback() {
            @Override
            public void onSuccess(String response) {
                LogModule.ECONOMY.log("Successfully posted transaction with uuid %s via webhook.".formatted(transaction.getUuid().toString()));
                future.complete(true);
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                LogModule.ECONOMY.warning("Posting transaction %s to the webhook failed. Status code: %s".formatted(transaction.getUuid().toString(), statusCode));
                LogModule.ECONOMY.warning("Error message: %s".formatted(errorMessage));
                future.complete(false);
            }
        };

        DiscordWebhook.sendMessage(callback, """
                **UUID**: %s
                **User ID**: %s
                **Amount**: %s
                **Item**: %s
                **Reason**: %s
                **State**: %s
                **Reason (if cancelled)**: %s
                """
                .formatted(transaction.getUuid().toString(), transaction.getUserId().toString(), transaction.getAmount(), transaction.getItemName(), transaction.getReason(), transaction.getState().toString(), cancelReason != null ? cancelReason : "none"));
        return future;
    }

    private static boolean deduct(Transaction transaction) {
        Player player = Bukkit.getPlayer(transaction.getUserId());
        if (player == null) {
            return false;
        }

        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            return false;
        }
        return EconomyManager.subtract(ctfPlayer, transaction.getAmount());
    }

    private static boolean refund(Transaction transaction) {
        UUID playerUuid = transaction.getUserId();
        int amount = transaction.getAmount();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
        if (offlinePlayer.isOnline()) {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(offlinePlayer.getName());
            if (ctfPlayer == null) {
                return false;
            }
            EconomyManager.increment(ctfPlayer, amount);
        } else {
            OfflineCTFPlayer offlineCTFPlayer = new OfflineCTFPlayer(playerUuid);
            EconomyManager.increment(offlineCTFPlayer, amount);
        }
        LogModule.ECONOMY.log("Refunded transaction %s for player %s.".formatted(transaction.getUuid().toString(), playerUuid.toString()));
        return true;
    }
}
