package me.joelsoep.ctf.server;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IDeleteOneSubscriber;
import me.joelsoep.ctf.database.subscriber.UnregisterServerSubscriber;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.server.listener.ServerListenerService;
import me.joelsoep.ctf.server.packet.PacketFactory;
import me.joelsoep.ctf.server.packet.packets.GoodbyePacket;
import me.joelsoep.ctf.server.packet.packets.HelloPacket;
import me.joelsoep.ctf.server.socket.SocketListener;
import me.joelsoep.ctf.server.socket.SocketSender;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Data
public class CTFServer {
    private final String id;
    private final String ip;
    private final int port;
    private final ServerMode mode;
    private final SocketListener socketListener;
    private final ServerListenerService listenerService;

    private ApiConfig apiConfig;
    private int games;
    private HashMap<String, BaseServer> onlineServers;

    public CTFServer(String id, String ip, int port, ServerMode mode) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.mode = mode;

        createApiConfig();

        PacketFactory.init();
        this.listenerService = new ServerListenerService("me.joelsoep.ctf.server.listener.listeners");
        this.socketListener = new SocketListener(port, apiConfig.key());
        this.onlineServers = new HashMap<>();

        ArrayList<Document> documents = MongoManager.findAll(MongoManager.getCollection("server")).join();
        documents.forEach(document -> {
            if (document.getString("_id").equals(this.id)) {
                return;
            }

            BaseServer baseServer = new BaseServer(
                    document.getString("_id"),
                    document.getString("ip"),
                    document.getInteger("port"),
                    ServerMode.valueOf(document.getString("mode"))
            );

            onlineServers.put(document.getString("_id"), baseServer);
        });
            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true);
            Bson update = Updates.combine(
                    Updates.set("port", port),
                    Updates.set("ip", ip),
                    Updates.set("mode", mode)
            );
            MongoManager.findOneAndUpdate(MongoManager.getCollection("server"), new Document("_id", id), update, options);
    }

    public void createApiConfig() {
        try {
            Document document = MongoManager.findOne(MongoManager.getCollection("config"), new Document("_id", 1)).get();
            apiConfig = new ApiConfig(document.getString("apiKey"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apiConfig.key() == null) {
            LogModule.CORE.severe("API CONFIG IS NULL!!!!!");
            return;
        }
        LogModule.CORE.log("Created API configuration with key %s".formatted(apiConfig.key()));
    }

    public CompletableFuture<Boolean> unregisterServer() {
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        MongoManager.deleteOne(
                MongoManager.getCollection("server"),
                new Document("_id", id),
                new UnregisterServerSubscriber(resultFuture)
        );

        return resultFuture;
    }

    public void hello() {
        LogModule.CORE.log("Sending hello packet to other servers.");
        HelloPacket packet = new HelloPacket.Builder()
                .withDefaultValues()
                .withMode(this.mode)
                .withPort(this.port)
                .withHost(this.ip)
                .build();
        SocketSender.sendAll(packet);
    }

    public void goodbye() {
        LogModule.CORE.log("Sending goodbye packet to other servers.");
        GoodbyePacket packet = new GoodbyePacket.Builder()
                .withDefaultValues()
                .withMode(this.mode)
                .withPort(this.port)
                .withHost(this.ip)
                .build();
        SocketSender.sendAll(packet);
    }

    public static boolean isEnabled() {
        return Main.getGameServer() != null;
    }

    public static void preloadShutdownModule() {
        new IDeleteOneSubscriber() {

            @Override
            public void onResult(DeleteResult result) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        };
        LogModule.CORE.log("Preloaded IDeleteOneSubscriber in JVM memory.");
    }

}
