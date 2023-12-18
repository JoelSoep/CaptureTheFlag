package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.map.MapLoader;
import me.joelsoep.ctf.game.board.Leaderboard;
import me.joelsoep.ctf.game.task.CleanItemsTask;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

public class WorldLoadListener implements Listener {

    private final Plugin plugin;

    public WorldLoadListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        Game game = Main.game;

        if (!world.getName().startsWith("ctf_")) {
            return;
        }
        game.createTeams(); // change when maps are implemented, createTeams should happen with args for coordinates etc
        MapLoader mapLoader = new MapLoader(plugin);
        mapLoader.load();
        mapLoader.placeFlag(game.getRed());
        mapLoader.placeFlag(game.getBlue());

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
