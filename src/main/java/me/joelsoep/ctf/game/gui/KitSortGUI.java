package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.subscriber.IFindOneSubscriber;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class KitSortGUI extends ChestGui {
    private final BaseKit kit;
    private final Player player;
    private final PatternPane background;
    private CTFPlayer ctfPlayer;
    private StaticPane itemPane;
    private HashMap<Integer, ItemStack> items;

    public KitSortGUI(Player player, BaseKit kit) {
        super(3, "Kit Sorting");
        this.player = player;
        this.kit = kit;

        this.itemPane = new StaticPane(0, 1, 9, 1, Pane.Priority.HIGH);
        this.background = new PatternPane(0, 0, 9, 3, Pane.Priority.LOW, new Pattern(
                "111111111",
                "000000000",
                "111121111"
        ));

        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        this.ctfPlayer = ctfPlayer;
        if (ctfPlayer.getData().getSortedKits().get(kit.getName()) == null) {
            this.items = new HashMap<>(kit.getItems());
        } else {
            this.items = ctfPlayer.getData().getSortedKits().get(kit.getName());
        }

        ItemStack saveStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta saveMeta = saveStack.getItemMeta();
        saveMeta.displayName(MM.deserialize("<green>Save Hotbar"));
        saveStack.setItemMeta(saveMeta);

        ItemStack backgroundPanes = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = backgroundPanes.getItemMeta();
        backgroundMeta.displayName(MM.deserialize(""));
        backgroundPanes.setItemMeta(backgroundMeta);

        this.background.bindItem('2', new GuiItem(saveStack, event -> {
            if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                event.setCancelled(true);
                return;
            }
            this.save();
        }));
        this.background.bindItem('1', new GuiItem(backgroundPanes));
        this.addPane(background);
        this.addPane(itemPane);
        openGUI();
    }

    private void openGUI() {
        this.items.forEach((integer, itemStack) -> {
            if (itemStack == null) {
                return;
            }
            this.itemPane.addItem(new GuiItem(itemStack), integer, 0);
        });
        this.update();
        this.show(player);

        this.setOnClose(event -> {
            // return in case the game is starting in order to prevent the hotbar inventory from overriding the kit.
            if (ctfPlayer.getGame() != null) {
                if (ctfPlayer.getGame().getState() != GameState.WAITING) {
                    return;
                }
            }
            player.getInventory().clear();
            new Hotbar(player);
            new KitSelectionGUI((Player) event.getPlayer(), Main.getKitService());
        });

        this.setOnGlobalDrag(event -> {
            ItemStack draggedItem = event.getOldCursor();
            if (draggedItem == null) {
                return;
            }

            Material material = draggedItem.getType();
            if (material == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
        });

        this.setOnGlobalClick(event -> {
            if (event.getClickedInventory() != event.getView().getTopInventory()) {
                event.setCancelled(true);
                return;
            }

            if (event.getClick() == ClickType.DROP || event.getClick() == ClickType.NUMBER_KEY || event.getClick() == ClickType.CONTROL_DROP) {
                event.setCancelled(true);
                return;
            }
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
                return;
            }
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
        });
    }

    private void save() {
        this.items = new HashMap<>();

        int index = 9;
        for (int relativePosition = 0; relativePosition < 9; relativePosition++) {
            ItemStack item = this.getInventory().getItem(index);
            this.items.put(relativePosition, item);
            index++;
        }

        /**
         * We ensure that the items that are put in the kit to be saved are also in the default sorted kit.
         * This is to avoid cheaters potentially spawning items in and saving them to a kit.
         */

        AtomicBoolean invalid = new AtomicBoolean(false);
        ArrayList<ItemStack> verifier = new ArrayList<>(kit.getItems().values());
        verifier.forEach(itemStack -> {
            if (!this.getInventory().contains(itemStack.getType())) {
                invalid.set(true);
                return;
            }
            AtomicReference<ItemStack> foundItem = new AtomicReference<>(null);
            this.getInventory().forEach(item -> {
                if (item == null) {
                    return;
                }
                if (item.getType() == itemStack.getType()) {
                    foundItem.set(item);
                    return;
                }
            });

            if (foundItem.get() == null) {
                return;
            }

            ItemStack finalFound = foundItem.get();
            if (finalFound.getAmount() != itemStack.getAmount()) {
                invalid.set(true);
                return;
            }
        });

        if (invalid.get()) {
            player.closeInventory();
            player.getInventory().clear();
            new Hotbar(player);
            player.sendMessage(MM.deserialize("<red>Invalid SaveState."));
            return;
        }

        this.items.forEach((integer, itemStack) -> { if (itemStack == null) { return; } LogModule.KIT.log("Index: " + integer + ", Type: " + itemStack.getType()); });

        if (ctfPlayer.getData().getSortedKits().get(kit.getName()) != null) {
            this.ctfPlayer.getData().getSortedKits().replace(kit.getName(), this.items);
        } else {
            this.ctfPlayer.getData().getSortedKits().put(kit.getName(), this.items);
        }

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(false);

        Document document = new Document();
        ArrayList<Document> documentArray = new ArrayList<>();
        if (getSerializedData() != null) {
            for (Map.Entry<String, HashMap<Integer, String>> entry : getSerializedData().entrySet()) {
                String kitName = entry.getKey();
                HashMap<Integer, String> kitData = entry.getValue();

                Document innerDocument = new Document();
                for (Map.Entry<Integer, String> innerEntry : kitData.entrySet()) {
                    Integer integer = innerEntry.getKey();
                    String material = innerEntry.getValue();

                    innerDocument.append(String.valueOf(integer), material);
                }
                documentArray.add(new Document(kitName, innerDocument));
            }

            document.append("sortedKits", documentArray);

            Document finalDoc = new Document("$set", document);

            Main.getMongoManager().findOneAndUpdate(Main.getMongoManager().getDatabase().getCollection("player"), Filters.eq("_id", player.getUniqueId().toString()), finalDoc, options, new IFindOneSubscriber<Document>() {
                @Override
                public void onResult(Document result) {
                    if (result != null) {
                        System.out.println(result);
                    } else {
                        System.out.println("Result is null");
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            player.sendMessage(MM.deserialize("<gray>Backed up data to database."));
        }

        player.closeInventory();
        player.getInventory().clear();
        new Hotbar(player);
        player.sendMessage(MM.deserialize("<green>Saved Hotbar!"));
    }

    private HashMap<String, HashMap<Integer, String>> getSerializedData() {
        HashMap<String, HashMap<Integer, ItemStack>> listToSerialize = ctfPlayer.getData().getSortedKits();
        HashMap<String, HashMap<Integer, String>> serializedList = new HashMap<>();
        if (listToSerialize == null) {
            return null;
        }

        for (String key : listToSerialize.keySet()) {
            HashMap<Integer, String> serialized = new HashMap<>();
            HashMap<Integer, ItemStack> kit = listToSerialize.get(key);
            for (Integer integer : kit.keySet()) {
                ItemStack itemStack = kit.get(integer);
                if (itemStack == null) {
                    continue;
                }
                String materialString = itemStack.getType().toString();
                serialized.put(integer, materialString);
            }
            serializedList.put(key, serialized);
        }
        return serializedList;
    }

}
