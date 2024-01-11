package me.joelsoep.ctf.game.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class CleanItemsTask {
    private final Plugin plugin;
    private final World world;
    private int id;

    public CleanItemsTask(Plugin plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        clean();
    }

    private void clean() {
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                world.getEntities().stream().filter(entity -> entity.getType() == EntityType.DROPPED_ITEM).forEach(Entity::remove);
            }
        }, 60L * 20L, 60L * 20L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
