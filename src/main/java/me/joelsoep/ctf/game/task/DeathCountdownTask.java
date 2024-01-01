package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.kit.kits.Warrior;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.plugin.Plugin;

public class DeathCountdownTask {

    private int id;
    private int seconds;
    private Plugin plugin;
    private CTFPlayer player;

    public DeathCountdownTask(Plugin plugin, CTFPlayer player, int seconds) {
        this.plugin = plugin;
        this.player = player;
        this.seconds = seconds;
        this.countdown();
    }

    public void countdown() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (player == null || !player.getPlayer().isOnline()) {
                    cancel();
                    return;
                }

                if (player.getTeam() == null) {
                    LogModule.GAME.severe("Player team is null @ DeathCountdownTask: " + player.getPlayer().getName());
                    cancel();
                    return;
                }

                if (seconds == 0) {
                    player.getPlayer().teleport(player.getTeam().getSpawn());
                    player.getPlayer().setGameMode(GameMode.ADVENTURE);
                    player.sendActionBar(MM.deserialize("<gray>Fight!"));
                    player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, 2));
                    if (player.getKit() == null) {
                        player.setKit(new Warrior());
                    }
                    Main.kitService.giveKitToPlayer(player.getPlayer(), player.getKit().getName());
                    cancel();
                }
                actionbar(seconds);
                seconds--;
            }
        }, 0L, 20L);
    }

    public void actionbar(int seconds) {
        player.sendActionBar(MM.deserialize("<gray>Respawning in <red>" + seconds));
        player.playSound(Sound.sound(Key.key("block.note_block.hat"), Sound.Source.MASTER, 1, 1));
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
