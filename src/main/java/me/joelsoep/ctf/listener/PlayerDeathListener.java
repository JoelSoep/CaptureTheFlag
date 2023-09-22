package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.PlayerRewardSpecialEvent;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.game.task.DeathCountdownTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.logging.Level;

public class PlayerDeathListener implements Listener {

    private final Plugin plugin;

    public PlayerDeathListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = player.getKiller();
        CTFPlayer ctfPlayer = Main.playerList.get(player.getName());

        if (ctfPlayer == null) {
            return;
        }

        if (ctfPlayer.getTeam() == null) {
            return;
        }

        Game game = ctfPlayer.getGame();
        if (game == null) {
            return;
        }

        player.playSound(Sound.sound(Key.key("entity.player.death"), Sound.Source.MASTER, 1, 1));
        if (player.getPlayer() != null) {
            for (PotionEffect potionEffect : player.getPlayer().getActivePotionEffects()) {
                PotionEffectType type = potionEffect.getType();
                player.getPlayer().removePotionEffect(type);
            }
        }

        if (game.getState() == GameState.RUNNING || game.getState() == GameState.READY) {
            Team team = ctfPlayer.getTeam();
            Team oppositeTeam = ctfPlayer.getTeam().equals(game.getRed()) ? game.getBlue() : game.getRed();
            MapLoader mapLoader = new MapLoader(plugin);

            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGlowing(false);
            ctfPlayer.setKillStreak(0);
            ctfPlayer.getData().setDeaths(ctfPlayer.getData().getDeaths() + 1);
            event.setCancelled(true);
            player.teleport(new Location(Bukkit.getWorld("CaptureTheFlag"), -118, -17, -97));
            new DeathCountdownTask(plugin, ctfPlayer, 5);

            if (oppositeTeam.getFlagThief() != null) {
                if (!(oppositeTeam.getFlagThief().equals(ctfPlayer))) {
                    return;
                }
                mapLoader.placeFlag(oppositeTeam);
                oppositeTeam.setFlagThief(null);
                game.getAudience().sendMessage(
                        MM.deserialize(
                                "<gray>Team %s<gray>'s flag has been returned."
                                        .formatted(oppositeTeam.getTeamColor().getPrefix()))
                );

            }
            if (killer != null) {
                CTFPlayer ctfKiller = Main.playerList.get(killer.getName());
                if (ctfKiller == null) {
                    return;
                }

                ctfKiller.setKillStreak(ctfKiller.getKillStreak() + 1);
                ctfKiller.getData().setKills(ctfKiller.getData().getKills() + 1);
                if (ctfKiller.getKillStreak() % game.getMinKillStreak() == 0) {
                    PlayerRewardSpecialEvent rewardEvent = new PlayerRewardSpecialEvent(ctfKiller);
                    Bukkit.getPluginManager().callEvent(rewardEvent);
                }
                if (killer.getHealth() >= 13) {
                    killer.setHealth(20);
                } else {
                    killer.setHealth(killer.getHealth() + 7);
                }

                game.getAudience().sendMessage(MM.deserialize("""
                        <gray>%s <gray>has been killed by %s<gray>!"""
                        .formatted(team.getTeamColor().getColor() + player.getName(), ctfKiller.getTeam().getTeamColor().getColor() + killer.getName())));
            } else {
                if (Main.entityDamageHandler.getLastDamager(player) != null) {
                    killer = Main.entityDamageHandler.getLastDamager(player);
                    EntityDamageEvent.DamageCause cause = Main.entityDamageHandler.getLastDamageCause(player);
                    if (cause != null) {
                        Bukkit.getLogger().info(cause.toString());
                        player.setKiller(killer);
                        player.setLastDamageCause(new EntityDamageEvent(player, cause, 0));
                        Main.entityDamageHandler.setLastDamager(player, null, null);

                        CTFPlayer ctfKiller = Main.playerList.get(killer.getName());
                        if (ctfKiller == null) {
                            return;
                        }

                        ctfKiller.setKillStreak(ctfKiller.getKillStreak() + 1);
                        ctfKiller.getData().setKills(ctfKiller.getData().getKills() + 1);
                        if (ctfKiller.getKillStreak() % game.getMinKillStreak() == 0) {
                            PlayerRewardSpecialEvent rewardEvent = new PlayerRewardSpecialEvent(ctfKiller);
                            Bukkit.getPluginManager().callEvent(rewardEvent);
                        }
                        if (killer.getHealth() >= 13) {
                            killer.setHealth(20);
                        } else {
                            killer.setHealth(killer.getHealth() + 7);
                        }

                        game.getAudience().sendMessage(MM.deserialize("""
                                <gray>%s <gray>has been killed by %s<gray>!"""
                                .formatted(team.getTeamColor().getColor() + player.getName(), ctfKiller.getTeam().getTeamColor().getColor() + killer.getName())));
                    }
                    return;
                }
                game.getAudience().sendMessage(MM.deserialize("""
                        <gray>%s <gray>died.
                        """.formatted(team.getTeamColor().getColor() + player.getName())));
            }
        } else {
            player.teleport(new Location(Bukkit.getWorld("CaptureTheFlag"), 516, 35, 13));
            event.setCancelled(true);
        }
    }
}
