package me.joelsoep.ctf.server.listener.listeners;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.server.listener.PacketHandler;
import me.joelsoep.ctf.server.listener.ServerListener;
import me.joelsoep.ctf.server.packet.packets.RetrievePlayerPacket;
import org.bukkit.Bukkit;

import java.util.UUID;

public class RetrievePlayerListener implements ServerListener {

    @PacketHandler
    public void onRequest(RetrievePlayerPacket packet) {
        PlayerService.getWhitelist().add(UUID.fromString(packet.getUuid()));
        LogModule.PLAYER.log("Added player %s to whitelist. Sending request for retrieval.".formatted(packet.getUuid()));
        String serverId = Main.getGameServer().getId();
        String playerName = Bukkit.getOfflinePlayer(UUID.fromString(packet.getUuid())).getName();
        Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "yeet %s %s".formatted(playerName, serverId));
            }
        });
    }
}
