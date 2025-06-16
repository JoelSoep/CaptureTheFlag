package me.joelsoep.ctf.animation.animations;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.game.special.extension.UltraShieldExtension;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class UltraShieldAnimation extends Animation {

    public UltraShieldAnimation(Location location, Long delay, Long interval) {
        super(location, delay, interval);
    }

    @Override
    protected int animate(Location location) {
        LogModule.SPECIAL.log("PLAYING RN");
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {

                Vector direction = location.getDirection().normalize();

                Location center = location.clone().add(direction);

                double radius = 3.0;   // breedte (straal)
                double height = 2.2;   // hoogte
                int xSteps = 10;       // minder = ruimer rooster
                int ySteps = 5;

                for (int xi = -xSteps; xi <= xSteps; xi++) {
                    for (int yi = -ySteps; yi <= ySteps; yi++) {
                        double xNorm = xi / (double) xSteps;   // -1 tot 1
                        double yNorm = yi / (double) ySteps;   // -1 tot 1

                        // Parabolische buiging, sterk en naar speler toe
                        double curveAmount = 1.4; // hoger = sterker gebogen
                        double z = curveAmount * (1 - xNorm * xNorm); // buiging naar speler toe (positieve Z)

                        double x = xNorm * radius;
                        double y = yNorm * height;

                        Vector localPoint = new Vector(x, y, z);
                        Location loc = transformLocalToWorld(center, localPoint, direction);
                        ArmorStand stand = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class, as -> {
                            as.setVisible(false);
                            as.setGravity(false);
                            as.setMarker(false); // belangrijk: zorgt dat er een hitbox is
                            as.setSmall(true);
                            as.setCollidable(true); // botsing aan
                            as.setInvulnerable(true);
                            as.setSilent(true);
                            as.setAI(false);
                            as.setCustomNameVisible(false);
                        });
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                stand.remove();
                            }
                        }, 8L);
                        location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 1, new Particle.DustTransition(Color.fromRGB(0, 102, 255), Color.fromRGB(51, 153, 255), 2));
                    }
                }
                setDuration(getDuration() - getInterval());
            }
        }, getDelay(), getInterval());
    }

    public Location transformLocalToWorld(Location origin, Vector localOffset, Vector direction) {
        Vector forward = direction.clone().normalize();
        Vector up = new Vector(0, 1, 0);

        Vector right = forward.clone().crossProduct(up).normalize();
        up = right.clone().crossProduct(forward).normalize(); // Recalculate real up for tilted view

        // Bouw wereldvector uit lokale X/Y/Z
        Vector worldOffset = new Vector(0, 0, 0);
        worldOffset.add(right.multiply(localOffset.getX()));
        worldOffset.add(up.multiply(localOffset.getY()));
        worldOffset.add(forward.multiply(localOffset.getZ()));

        return origin.clone().add(worldOffset);
    }

    public void killParticles() {

    }

    public Vector rotateVector(Vector vector, Vector direction) {
        // Richting -> Euler rotatie (alleen yaw en pitch, geen roll)
        Location loc = new Location(null, 0, 0, 0);
        loc.setDirection(direction);
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        double yawRad = Math.toRadians(-yaw);
        double pitchRad = Math.toRadians(-pitch);

        double x1 = vector.getX() * Math.cos(yawRad) - vector.getZ() * Math.sin(yawRad);
        double z1 = vector.getX() * Math.sin(yawRad) + vector.getZ() * Math.cos(yawRad);
        double y1 = vector.getY();

        double y2 = y1 * Math.cos(pitchRad) - z1 * Math.sin(pitchRad);
        double z2 = y1 * Math.sin(pitchRad) + z1 * Math.cos(pitchRad);

        return new Vector(x1, y2, z2);
    }
}
