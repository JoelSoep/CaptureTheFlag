package me.joelsoep.ctf.autoBroadcast;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class BroadcastMessage {

    private String message;
    private String sound;
    private Long duration;

    public static BroadcastMessage builder() {
        return new BroadcastMessage();
    }

    public BroadcastMessage withMessage(String message) {
        this.message = message;
        return this;
    }

    public BroadcastMessage withSound(String sound) {
        this.sound = sound;
        return this;
    }

    public BroadcastMessage withDuration(Integer duration) {
        this.duration = duration.longValue();
        return this;
    }

    public void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (message == null) {
                    throw new IllegalStateException("No message found for broadcast message.");
                }

                if (duration == null) {
                    throw new IllegalStateException("No duration found for broadcast message.");
                }

                PlayerService.getAudience().sendMessage(MM.deserialize(message));

                if (sound != null) {
                    Bukkit.getOnlinePlayers().stream()
                            .filter(player -> player.getWorld().getName().equals(MapManager.getLobby().getWorld().getName()))
                            .forEach(player -> player.playSound(Sound.sound(Key.key(sound), Sound.Source.MASTER, 1, 1)));
                }

            }
        }, 0L, duration);
    }


}
