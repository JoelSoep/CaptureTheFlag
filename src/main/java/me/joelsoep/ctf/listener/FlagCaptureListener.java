package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.FlagCaptureEvent;
import me.joelsoep.ctf.game.event.FlagReturnEvent;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamColor;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class FlagCaptureListener implements Listener {

    @EventHandler
    public void onCapture(FlagCaptureEvent event) {
        Game game = event.getGame();
        CTFPlayer ctfPlayer = event.getCtfPlayer();
        Player player = ctfPlayer.getPlayer();
        Team team = ctfPlayer.getTeam();
        Team flagOwner = event.getFlagOwner();

        if (team == null) {
            LogModule.GAME.severe(player.getName() + "\\'s team is null @ flag capture!");
            return;
        }

        Location flagLocation = team.getFlag().getLocation();
        Location[] locations = new Location[25];
        Location calc = flagLocation.clone().subtract(2, 0, 2);
        int r = 0;
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                locations[r] = calc.clone().add(x, 0, z);
                r++;
            }
        }

        System.out.println(locations.length);

        final List<String> DESTROYABLE_BLOCKS = List.of(
                "BLUE_CONCRETE",
                "RED_CONCRETE",
                "BLUE_CONCRETE_POWDER",
                "RED_CONCRETE_POWDER"
        );

        for (int i = 0; i < 20; i++) {
            int finalI = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (Location location : locations.clone()) {
                        Location check = location.clone().add(0, finalI, 0);
                        if (DESTROYABLE_BLOCKS.contains(check.getBlock().getType().toString())) {
                            check.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, check, 1, check.getBlock().getBlockData());
                            check.getWorld().playSound(check, "minecraft:block.stone.break", SoundCategory.BLOCKS, 10, 1);
                            check.getWorld().getBlockAt(check).setType(Material.AIR);
                        }
                    }
                }
            }, (long) i);
        }

        game.getMap().resetFeatures();
        team.setPoints(team.getPoints() + 1);
        game.getXPCollector().write(ctfPlayer, 100);
        game.getAudience().sendMessage(MM.deserialize("""
                <gray>Team %s<gray>'s flag has been captured by %s<gray>!"""
                .formatted(flagOwner.getTeamColor().getPrefix(), team.getTeamColor().getColor() + player.getName())));
        game.getAudience().playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 1, 1));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
        player.getInventory().setHelmet(BaseKit.getHelmet(ctfPlayer.getTeam().getTeamColor() == TeamColor.BLUE ? Color.BLUE : Color.RED));
        ctfPlayer.getData().setCaptures(ctfPlayer.getData().getCaptures() + 1);

        FlagReturnEvent returnEvent = new FlagReturnEvent(game, flagOwner, ctfPlayer);
        Bukkit.getPluginManager().callEvent(returnEvent);
    }
}
