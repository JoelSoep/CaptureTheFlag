package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.board.LeaderboardService;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.MapState;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.map.type.LobbyMap;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.logging.Level;

public class WorldLoadListener implements Listener {

    private final Plugin plugin;

    public WorldLoadListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        Game game = Main.getGame();

        if (!world.getName().toLowerCase(Locale.ROOT).startsWith("plugins/CapturetheFlag/maps/".toLowerCase(Locale.ROOT))) {
            LogModule.CORE.log("Excluding world %s from ctf-registry".formatted(world.getName()));
            return;
        }

        LogModule.CORE.log("Loading world %s".formatted(world.getName()));
        LogModule.MAP.log("Loaded maps rn: %s".formatted(MapManager.getGameMaps().size()));

        /**
         * We attempt to load the world here as well as on map initialization, since it's a race condition:
         * The world could load earlier than the map was initialized, or the map loads earlier than the world was initialized.
         * To handle this, I attempt to load them on world load as well as on map initialization.
         */

        MapManager.getGameMaps().forEach(map -> {
            World mapWorld = map.getWorld();
            if (mapWorld == null) {
                LogModule.MAP.log(Level.WARNING, "Couldn't load map %s because it's world is null!".formatted(map.getName()));
                return;
            }

            if (!mapWorld.getName().equals(world.getName())) {
                if (map.getState() == MapState.READY) {
                    return;
                }

                map.load();
                map.setState(MapState.READY);
                LogModule.MAP.log("Added content to map %s.".formatted(map.getName()));
            }
        });

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.FALL_DAMAGE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
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
        LogModule.CORE.log("Updated gamerules for world %s.".formatted(world.getName()));
    }
}
