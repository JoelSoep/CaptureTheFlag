package me.joelsoep.ctf.server.listener.listeners;

import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.server.listener.PacketHandler;
import me.joelsoep.ctf.server.listener.ServerListener;
import me.joelsoep.ctf.server.packet.packets.AlertPacket;
import me.joelsoep.ctf.util.MM;

public class AlertListener implements ServerListener {

    @PacketHandler
    public void onRequest(AlertPacket packet) {
        String message = packet.getMessage();
        if (message == null) {
            message = "";
        }
        PlayerService.getAudience().sendMessage(MM.alert(message));
    }
}
