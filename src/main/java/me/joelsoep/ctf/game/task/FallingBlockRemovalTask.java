package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public class FallingBlockRemovalTask {

    private final Entity block;
    private int id;

    public FallingBlockRemovalTask(@Nullable Entity block) {
        this.block = block;
        check(block);
    }

    public void check(@Nullable Entity block) {
         this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
             @Override
             public void run() {
                 if (block == null) {
                     LogModule.CORE.log("entity is null");
                     return;
                 }

                 if (block.isValid()) {
                     LogModule.CORE.log("invalidated entity");
                     return;
                 }
                 scheduleBlockDeletion(block.getLocation());
                 cancel();
             }
         }, 0L, 1L);
    }

    public void scheduleBlockDeletion(Location location) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                Block block = location.getBlock();

                if (block.getType() != Material.BLUE_CONCRETE_POWDER && block.getType() != Material.RED_CONCRETE_POWDER) {
                    return;
                }

                World world = block.getWorld();
                // don't ask me why, but if we just try to block.setType() it won't work.
                world.getBlockAt(block.getLocation()).setType(Material.AIR);
                world.spawnParticle(Particle.ANGRY_VILLAGER, block.getLocation(), 1);
            }
        },  0L);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
