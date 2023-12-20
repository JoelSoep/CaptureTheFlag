package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.board.Leaderboard;
import me.joelsoep.ctf.game.task.CleanItemsTask;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.MapState;
import me.joelsoep.ctf.map.type.GameMap;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public class WorldLoadListener implements Listener {

    private final Plugin plugin;

    public WorldLoadListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        Game game = Main.game;

        if (!world.getName().toLowerCase(Locale.ROOT).startsWith("plugins/CapturetheFlag/maps/ctf_".toLowerCase(Locale.ROOT))) {
            Bukkit.getLogger().info("Excluding world %s from ctf-registry".formatted(world.getName()));
            return;
        }

        Bukkit.getLogger().info("Loaded maps rn: %s".formatted(MapManager.getGameMaps().size()));

        /**
         * We attempt to load the world here as well as on map initialization, since it's a race condition:
         * The world could load earlier than the map was initialized, or the map loads earlier than the world was initialized.
         * To handle this, I attempt to load them on world load as well as on map initialization.
         */

        MapManager.getGameMaps().forEach(map -> {
            World mapWorld = map.getWorld();
            if (mapWorld == null) {
                Bukkit.getLogger().warning("Couldn't load map %s because it's world is null!".formatted(map.getName()));
                return;
            }

            if (!mapWorld.getName().equals(world.getName())) {
                if (!(map instanceof GameMap gameMap)) {
                    return;
                }
                gameMap.load();
                gameMap.setState(MapState.READY);
                Bukkit.getLogger().info("Added content to map %s.".formatted(gameMap.getName()));
            }
        });

        //TODO: move placeFlag() here to ensure that the world is loaded
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true); // change when implementing custom advancements.
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRule.MOB_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.TNT_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRule.BLOCK_EXPLOSION_DROP_DECAY, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DROWNING_DAMAGE, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, true);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);

        new CleanItemsTask(plugin, world);

        new Leaderboard(plugin,"kill_leaderboard", "&4&l☠ &c&lKills Leaderboard &4&l☠", new Location(world, 505, 39, 3), Main.mongo.getUserCollection(), "player_kills", 0, true, "&4", "&c");
        new Leaderboard(plugin, "kd_leaderboard", "&3&l♠ &b&lK/D Leaderboard &3&l♠", new Location(world, 505, 39, -4), Main.mongo.getUserCollection(), "player_kd", (double) 1, false, "&3", "&b");
    }
}
