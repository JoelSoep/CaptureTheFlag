package me.joelsoep.ctf.server.listener.listeners;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.server.BaseServer;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.server.listener.PacketHandler;
import me.joelsoep.ctf.server.listener.ServerListener;
import me.joelsoep.ctf.server.packet.packets.HelloPacket;

public class HelloListener implements ServerListener {

    @PacketHandler
    public void onRequest(HelloPacket packet) {
        CTFServer server = Main.getGameServer();
        BaseServer baseServer = new BaseServer(
                packet.getSender(),
                packet.getHost(),
                packet.getPort(),
                packet.getMode()
        );
        server.getOnlineServers().put(packet.getSender(), baseServer);
        LogModule.CORE.log("Received first light from server %s. They have been added to the server registry.".formatted(packet.getSender()));
    }
}
