package me.joelsoep.ctf.server.socket;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.server.packet.CTFPacket;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketSender {

    public static void send(String host, int port, @NotNull CTFPacket packet) {
        LogModule.CORE.log("Sending packet %s to %s.".formatted(packet.getAction(), host + ":" + port));
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            JSONObject data = packet.toJSON();
            LogModule.CORE.log("Sending: " + data.toString());

            out.println(data.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendAll(@NotNull CTFPacket packet) {
        LogModule.CORE.log("Sending packet %s to all servers.".formatted(packet.getAction()));
        CTFServer server = Main.getGameServer();
        server.getOnlineServers().values().forEach(onlineServer -> {
            String ip = onlineServer.ip();
            int port = onlineServer.port();
            send(ip, port, packet);
        });
    }

    public static void sendAll(@NotNull CTFPacket packet, boolean includeSelf) {
        sendAll(packet);

        if (!includeSelf) {
            return;
        }

        CTFServer server = Main.getGameServer();
        String host = server.getIp();
        int port = server.getPort();
        send(host, port, packet);
    }

}
