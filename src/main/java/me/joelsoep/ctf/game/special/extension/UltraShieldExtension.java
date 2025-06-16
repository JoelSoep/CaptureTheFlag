package me.joelsoep.ctf.game.special.extension;

import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.game.special.specials.UltraShield;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UltraShieldExtension {
    @Getter
    private static final HashMap<Player, UltraShieldData> activeShields = new HashMap<>();

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
