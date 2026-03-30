package me.joelsoep.ctf.cosmetic.type.captureAnimation;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.CaptureAnimationCosmetic;
import me.joelsoep.ctf.util.MM;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FoundersFlameCaptureAnimation extends CaptureAnimationCosmetic {

    public FoundersFlameCaptureAnimation() {
        super("FoundersFlame", CosmeticRarity.LEGENDARY);
        this.setLogo(Material.CAMPFIRE);
        this.setDisplayName(MM.deserialize("Founders' Flame"));
    }

    @Override
    protected Animation getCaptureAnimation() {
        return new Animation() {
            @Override
            protected int animate(Location location) {
                BlockDisplay fireDisplay;
                final int[] tick = {0};

// Center and raise the fire block visually
                Location spawnLocation = location.clone().add(0, 2, 0);

                fireDisplay = spawnLocation.getWorld().spawn(spawnLocation, BlockDisplay.class, display -> {
                    display.setBlock(Material.FIRE.createBlockData());
                    display.setGravity(false);
                    display.setBrightness(new BlockDisplay.Brightness(15, 15));
                });

                spawnLocation.getWorld().playSound(spawnLocation, Sound.BLOCK_FIRE_AMBIENT, 10F, 1F);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (tick[0] >= 70) {
                            spawnLocation.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, fireDisplay.getLocation(), 10);
                            spawnLocation.getWorld().playSound(fireDisplay.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 10F, 1F);
                            fireDisplay.remove();
                            cancel();
                            return;
                        }

                        // Simple smooth up/down hover using sine wave
                        double hoverY = Math.sin(tick[0] * 0.1) * 0.1;
                        fireDisplay.teleport(spawnLocation.clone().add(0, hoverY, 0));

                        tick[0]++;
                    }
                }.runTaskTimer(Main.getInstance(), 0L, 1L);

                return 0;
            }
        };

    }
}
