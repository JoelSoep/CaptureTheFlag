package me.joelsoep.ctf.autoBroadcast;

import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.intellij.lang.annotations.Subst;

public class BroadcastMessage {

    private final Plugin plugin;
    private String message;
    private String sound;
    private Long duration;


    public BroadcastMessage(Plugin plugin) {
        this.plugin = plugin;
    }

    public BroadcastMessage builder() {
        return this;
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
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
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(Sound.sound(Key.key(sound), Sound.Source.MASTER, 1, 1)));
                }

            }
        }, 0L, duration);
    }


}
