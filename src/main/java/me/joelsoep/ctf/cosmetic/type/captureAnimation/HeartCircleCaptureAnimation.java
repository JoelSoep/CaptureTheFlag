package me.joelsoep.ctf.cosmetic.type.captureAnimation;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.CaptureAnimationCosmetic;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class HeartCircleCaptureAnimation extends CaptureAnimationCosmetic {

    public HeartCircleCaptureAnimation() {
        super("HeartCircle", CosmeticRarity.COMMON);
    }

    @Override
    protected Animation getCaptureAnimation() {
        return new Animation() {
            @Override
            protected int animate(Location location) {
                location = location.clone().toCenterLocation().add(0, 1,0 );
                Location finalLocation = location;
                new BukkitRunnable() {
                    int tick = 0;

                    @Override
                    public void run() {
                        if (tick >= 40) {
                            cancel();
                            return;
                        }

                        // Calculate angle: 0 to 2π
                        double angle = (2 * Math.PI * tick) / 40;

                        // Compute x and z offset in a circle
                        double xOffset = 1.5 * Math.cos(angle);
                        double zOffset = 1.5 * Math.sin(angle);

                        // Particle location
                        Location particleLoc = finalLocation.clone().add(xOffset, 0, zOffset);
                        finalLocation.getWorld().spawnParticle(Particle.HEART, particleLoc, 1, 0, 0, 0, 0);

                        tick++;
                    }
                }.runTaskTimer(Main.getInstance(), 0L, 1L); // Run every tick
                return 0;
            }
        };
    }
}
