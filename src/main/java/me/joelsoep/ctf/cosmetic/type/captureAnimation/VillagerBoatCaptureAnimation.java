package me.joelsoep.ctf.cosmetic.type.captureAnimation;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.CaptureAnimationCosmetic;
import me.joelsoep.ctf.util.SoundUtils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class VillagerBoatCaptureAnimation extends CaptureAnimationCosmetic {
    private int id;
    public VillagerBoatCaptureAnimation() {
        super("VillagerBoat", CosmeticRarity.MYTHIC);
    }

    @Override
    protected Animation getCaptureAnimation() {
        return new Animation() {
            @Override
            protected int animate(Location location) {
                location = location.clone().toCenterLocation().add(0, 2, 0);
                TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.TNT);
                tnt.setIsIncendiary(false);
                tnt.setFuseTicks(50);
                tnt.setGravity(false);
                tnt.setYield(0);
                tnt.getPersistentDataContainer().set(
                        new NamespacedKey(Main.getInstance(), "no_damage"),
                        PersistentDataType.BYTE,
                        (byte) 1
                );

                location = location.clone().toCenterLocation().add(0, 1, 0);
                Boat boat = (Boat) location.getWorld().spawnEntity(location, EntityType.OAK_BOAT);
                boat.setGravity(false);
                boat.setInvulnerable(true);

                Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
                ZombieVillager villager2 = (ZombieVillager) location.getWorld().spawnEntity(location, EntityType.ZOMBIE_VILLAGER);
                villager.setInvulnerable(true);
                villager2.setInvulnerable(true);
                boat.addPassenger(villager);
                boat.addPassenger(villager2);

                 id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        if (tnt.isDead()) {
                            VillagerBoatCaptureAnimation.this.cancel(id);
                            return;
                        }
                        tnt.getWorld().playSound(tnt.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 10F, 1F);
                    }
                }, 2L, 2L);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        villager.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 100, true));
                        villager2.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 100, true));
                        boat.setGravity(true);
                        boat.setVelocity(boat.getVelocity().add(new Vector(0, 1, 0).multiply(7)));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                Firework firework = (Firework) boat.getWorld().spawnEntity(boat.getLocation(), EntityType.FIREWORK_ROCKET);
                                FireworkMeta meta = firework.getFireworkMeta();
                                meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(Color.GREEN, Color.FUCHSIA).build());
                                firework.setFireworkMeta(meta);
                                firework.detonate();
                                villager.remove();
                                villager2.remove();
                                boat.remove();
                            }
                        }, 20L);
                    }
                }, 50L);
                return 0;
            }
        };
    }

    private void cancel(int id) {
        Bukkit.getScheduler().cancelTask(id);
    }
}
