package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.util.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ServerLoadListener implements Listener {

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        List<World> worlds = Bukkit.getWorlds();
        AtomicBoolean hasCTFWorld = new AtomicBoolean(false);

        worlds.forEach(world -> {
            if (world.getName().equals("CaptureTheFlag")) {
                hasCTFWorld.set(true);
            }
        });

        if (!hasCTFWorld.get()) {
            Bukkit.getLogger().log(Level.INFO, "Could not find a proper world for Capture the Flag. Please wait while we are setting up things...");
            WorldCreator worldCreator = new WorldCreator("CaptureTheFlag");
            worldCreator.type(WorldType.FLAT);
            worldCreator.generator(new EmptyChunkGenerator());
            Bukkit.createWorld(worldCreator);
        }
    }
}
