package me.joelsoep.ctf.server.socket;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.server.BaseServer;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.server.ServerMode;
import me.joelsoep.ctf.server.packet.PacketFactory;
import me.joelsoep.ctf.server.packet.packets.AlertPacket;
import me.joelsoep.ctf.server.packet.CTFPacket;
import me.joelsoep.ctf.server.packet.packets.HelloPacket;
import me.joelsoep.ctf.server.packet.packets.RetrievePlayerPacket;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class SocketListener implements Runnable {
    private final int port;
    private final String key;

    public SocketListener(int port, String key) {
        this.port = port;
        this.key = key;
        new Thread(this, "SocketListener-" + port).start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Example JSON message
                JSONObject data = new JSONObject(inputLine);
                System.out.println("Received: " + data);
                String action = data.getString("action");
                String key = data.getString("key");

                if (!key.equals(this.key)) {
                    return;
                }

                CTFPacket packet = PacketFactory.fromJSON(action, data);
                if (packet == null) {
                    LogModule.CORE.warning("Packet is null!! %s".formatted(data.toString()));
                    return;
                }

                // send packet to listeners
                Main.getGameServer().getListenerService().dispatchPacket(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
