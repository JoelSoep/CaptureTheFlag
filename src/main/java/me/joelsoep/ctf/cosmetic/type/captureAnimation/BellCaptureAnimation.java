package me.joelsoep.ctf.cosmetic.type.captureAnimation;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.CaptureAnimationCosmetic;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Bell;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

public class BellCaptureAnimation extends CaptureAnimationCosmetic {
    public BellCaptureAnimation() {
        super("Ring the Bell", CosmeticRarity.COMMON);
    }

    @Override
    protected Animation getCaptureAnimation() {
        return new Animation() {
            @Override
            protected int animate(Location location) {
                Location textDisplayLocation = location.clone().add(0, 4, 0);
                Location fenceLocation = location.clone().add(0, 3, 0);
                Location bellLocation = location.clone().add(0, 2, 0);
                fenceLocation.getWorld().getBlockAt(fenceLocation).setType(Material.SPRUCE_FENCE);
                bellLocation.getWorld().getBlockAt(bellLocation).setType(Material.BELL);

                TextDisplay textDisplay = textDisplayLocation.getWorld().spawn(textDisplayLocation, TextDisplay.class, entity -> {
                    entity.text(MM.deserialize("<bold><green>SLAM THAT BELL!!!"));
                    entity.setSeeThrough(true);
                    entity.setAlignment(TextDisplay.TextAlignment.CENTER);
                });

                Bell bell = (Bell) bellLocation.getBlock();

                final int[] tick = {0};
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (tick[0] == 50) {
                            textDisplay.remove();
                            fenceLocation.getBlock().setType(Material.AIR);
                            bellLocation.getBlock().setType(Material.AIR);
                            cancel();
                        }

                        bell.ring();
                        tick[0]++;
                    }
                }.runTaskTimer(Main.getInstance(), 5L, 10L);
                return 0;
            }
        };
    }
}
