package me.joelsoep.ctf.server.listener.listeners;

import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.server.listener.PacketHandler;
import me.joelsoep.ctf.server.listener.ServerListener;
import me.joelsoep.ctf.server.packet.packets.StoreReloadPacket;
import me.joelsoep.ctf.store.StoreManager;
import me.joelsoep.ctf.util.MM;

public class StoreReloadListener implements ServerListener {

    @PacketHandler
    public void onRequest(StoreReloadPacket packet) {
        PlayerService.getAudience().sendMessage(MM.alert("We are currently refreshing the store. Please refrain from making any purchases until further notice."));
        PlayerService.getAudience().sendMessage(MM.alert("During this refresh, server performance may be impacted until the process has been completed."));
        StoreManager.reload();
        PlayerService.getAudience().sendMessage(MM.alert("The store refresh has been completed. Thank you for your patience."));
    }
}
