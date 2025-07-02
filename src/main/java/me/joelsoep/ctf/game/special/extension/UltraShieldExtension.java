package me.joelsoep.ctf.game.special.extension;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class UltraShieldExtension {
    @Getter
    private static final HashMap<Player, UltraShieldData> ACTIVE_ANIMATING_SHIELDS = new HashMap<>(); // shields which are actively blocking
    @Getter private static final HashMap<Player, ItemExpiryManager> OWNED_SHIELDS = new HashMap<>();

    @Getter
    @Setter
    public static class UltraShieldData {
        public Location center;
        public Vector direction;
        public long expirationTime;

        public UltraShieldData(Location center, Vector direction, long lifetimeTicks) {
            this.center = center;
            this.direction = direction.normalize();
            this.expirationTime = System.currentTimeMillis() + (lifetimeTicks * 50L);
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    @Getter
    @Setter
    public static class ItemExpiryManager {
        private long expiryTime = 7 * 20L;
        private BukkitTask task;// 7 seconds in game

        private boolean isExpired() {
            return this.expiryTime <= 0L;
        }

        private void stopTicking() {
            this.task.cancel();
        }

        private void startTicking() {
            task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (getExpiryTime() == 0) {
                        stopTicking();
                    }
                    setExpiryTime(getExpiryTime() - 1L);
                }
            }, 0L, 1L);
        }

        private void removeShield() {
            stopTicking();
        }
    }

    public static boolean blocksLineOfAttack(UltraShieldData shield, Vector from, Vector to) {
        Vector shieldToAttacker = from.clone().subtract(shield.getCenter().toVector());
        Vector shieldToTarget = to.clone().subtract(shield.getCenter().toVector());

        // Projectie in richting van schild
        double dot1 = shield.direction.dot(shieldToAttacker);
        double dot2 = shield.direction.dot(shieldToTarget);

        // Als dot1 en dot2 tegengesteld zijn, staan aanvaller en doelwit aan weerszijden
        return dot1 * dot2 < 0;
    }

}
