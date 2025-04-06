package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.FlagReturnEvent;
import me.joelsoep.ctf.game.event.PlayerRewardSpecialEvent;
import me.joelsoep.ctf.game.task.DeathCountdownTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.KillStreakHandler;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = player.getKiller();
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        event.deathMessage(null);

        if (ctfPlayer == null) {
            LogModule.GAME.log("ctf player is null on death event: %s".formatted(player.getName()));
            return;
        }

        if (ctfPlayer.getTeam() == null) {
            LogModule.GAME.log("ctf team for player %s is null @ DeathEvent".formatted(player.getName()));
            return;
        }

        Game game = ctfPlayer.getGame();
        if (game == null) {
            LogModule.GAME.log("game is null! at player death event: %s".formatted(player.getName()));
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

            player.setGameMode(GameMode.SPECTATOR);
            player.getOpenInventory().close();
            player.getInventory().clear();
            clearCraftingSlots(player);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGlowing(false);
            ctfPlayer.setKillStreak(0);
            ctfPlayer.getData().setDeaths(ctfPlayer.getData().getDeaths() + 1);
            event.setCancelled(true);
            player.teleport(game.getMap().getCenterLocation());
            new DeathCountdownTask(ctfPlayer, 5);

            if (oppositeTeam.getFlag().getThief() != null) {
                if (!(oppositeTeam.getFlag().getThief().equals(ctfPlayer))) {
                    return;
                }
                FlagReturnEvent returnEvent = new FlagReturnEvent(game, oppositeTeam, player);
                Bukkit.getPluginManager().callEvent(returnEvent);

                game.getAudience().sendMessage(
                        MM.deserialize(
                                "<gray>Team %s<gray>'s flag has been returned."
                                        .formatted(team.getTeamColor().getPrefix()))
                );
            }
            if (killer != null) {
                CTFPlayer ctfKiller = PlayerService.getPlayers().get(killer.getName());
                if (ctfKiller == null) {
                    return;
                }
                if (killer.getName().equals(player.getName())) {
                    game.getAudience().sendMessage(MM.deserialize("""
                        <gray>%s <gray>died.
                        """.formatted(team.getTeamColor().getColor() + player.getName())));
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

                Cosmetic cosmetic = ctfKiller.getCosmetics().getAppliedCosmetic(CosmeticType.DEATH_MESSAGE);
                if (cosmetic == null) {
                    game.getAudience().sendMessage(MM.deserialize("""
                        <gray>%s <gray>has been killed by %s<gray>!"""
                            .formatted(team.getTeamColor().getColor() + player.getName(), ctfKiller.getTeam().getTeamColor().getColor() + killer.getName())));
                    return;
                }

                new KillStreakHandler(ctfKiller);
                game.getXPCollector().write(ctfKiller, 15);
            } else {
                game.getAudience().sendMessage(MM.deserialize("""
                        <gray>%s <gray>died.
                        """.formatted(team.getTeamColor().getColor() + player.getName())));
            }
        } else if (game.getState() == GameState.ENDED) {
            player.teleport(ctfPlayer.getTeam().getSpawn());
        } else {
            player.teleport(MapManager.getLobby().getSpawnLocation());
            new Hotbar(player);
            event.setCancelled(true);
        }
    }

    private void clearCraftingSlots(Player player) {
        for (int i = 0; i < 5; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) {
                continue;
            }
            LogModule.CORE.log("yep " + i);
            item.setType(Material.AIR);
        }
    }
}
