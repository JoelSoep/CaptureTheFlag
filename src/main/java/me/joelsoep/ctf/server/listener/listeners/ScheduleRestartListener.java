package me.joelsoep.ctf.server.listener.listeners;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.server.listener.PacketHandler;
import me.joelsoep.ctf.server.listener.ServerListener;
import me.joelsoep.ctf.server.packet.packets.ScheduleRestartPacket;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;

public class ScheduleRestartListener implements ServerListener {

    @PacketHandler
    public void onRequest(ScheduleRestartPacket packet) {
        if (Main.getGame().getState() == GameState.WAITING || Main.getGame().getState() == GameState.ENDED) {
            PlayerService.getAudience().sendMessage(MM.alert("Restarting server in 10 seconds."));
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Main.getInstance().getServer().restart();
                }
            }, 20L * 10);
        } else {
            Main.getSettings().setRestartAfterGame(true);
            PlayerService.getAudience().sendMessage(MM.alert(
                    "A server restart has been scheduled. As soon as the current game ends, the server will restart."
            ));
        }
    }
}
