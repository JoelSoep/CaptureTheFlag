package me.joelsoep.ctf.animation.animations;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.Animation;
import org.bukkit.*;

public class BreakAnimation extends Animation {

    private final double x;
    private final double y;
    private final double z;
    private int ticks = 0;

    public BreakAnimation(Location location) {
        super(location);

        this.x = location.getX();
        this.y = location.getY() + 0.5;
        this.z = location.getZ();
    }

    @Override
    protected int animate(Location location) {
        World world = location.getWorld();
        double radius = 1.5;

        return Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                double angle = 1.5 * Math.PI * ticks / 20; // 20 ticks per second, 1 full circle every second
                double circleX = x + radius * Math.cos(angle);
                double circleZ = z + radius * Math.sin(angle);
                double circleY = y; // Keep y constant for a flat circle

                Particle particle = Particle.DUST_COLOR_TRANSITION;
                world.spawnParticle(particle, circleX, circleY, circleZ, 5, new Particle.DustOptions(Color.fromRGB(245, 42, 214), 1));

                ticks++; // Increment the tick counter
            }
        }, this.getDelay(), this.getInterval());
    }
}
