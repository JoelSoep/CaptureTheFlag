package me.joelsoep.ctf.server.listener.listeners;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.server.listener.PacketHandler;
import me.joelsoep.ctf.server.listener.ServerListener;
import me.joelsoep.ctf.server.packet.packets.GoodbyePacket;

public class GoodbyeListener implements ServerListener {

    @PacketHandler
    public void onGoodbye(GoodbyePacket packet) {
        LogModule.CORE.log("Received goodbye packet from server %s. Removing server from server registry.".formatted(packet.getSender()));
        Main.getGameServer().getOnlineServers().remove(packet.getSender());
    }
}
