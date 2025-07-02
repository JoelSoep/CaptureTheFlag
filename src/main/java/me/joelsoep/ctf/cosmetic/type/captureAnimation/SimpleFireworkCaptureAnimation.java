package me.joelsoep.ctf.cosmetic.type.captureAnimation;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.CaptureAnimationCosmetic;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class SimpleFireworkCaptureAnimation extends CaptureAnimationCosmetic {
    public SimpleFireworkCaptureAnimation() {
        super("SimpleFirework", CosmeticRarity.COMMON);
        setLogo(Material.FIREWORK_ROCKET);
    }

    @Override
    protected Animation getCaptureAnimation() {
        return new Animation() {
            @Override
            protected int animate(Location location) {

                location = location.clone().add(0, 2, 0);
                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(Color.YELLOW)
                        .withFlicker()
                        .withTrail()
                        .with(FireworkEffect.Type.BALL)
                        .build();
                Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
                System.out.println(firework.getLocation());
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                meta.setPower(1);
                firework.setInvulnerable(true);
                firework.setFireworkMeta(meta);
                return 0;
            }
        };
    }
}
