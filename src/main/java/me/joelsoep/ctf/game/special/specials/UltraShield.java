package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.animation.animations.UltraShieldAnimation;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.game.special.extension.UltraShieldExtension;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UltraShield extends BaseSpecial<PlayerInteractEvent> {
    public UltraShield() {
        super("UltraShield");
    }
    private final List<Integer> id = new ArrayList<>();

    @Override
    protected ItemStack getItemStack() {
        return new ItemBuilder(Material.SHIELD)
                .setName(MM.deserialize("<blue>Ultra Shield"))
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .enchant(Enchantment.DURABILITY, 1, true)
                .setCustomModelData(732)
                .setHealth(1)
                .build();
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player attacker)) return;
        if (!(event.getDamager() instanceof LivingEntity target)) return;

        Location attackerLoc = attacker.getEyeLocation();
        Location targetLoc = target.getLocation().add(0, 1, 0); // middelpunt van torso

        for (UltraShieldExtension.UltraShieldData shield : UltraShieldExtension.getActiveShields().values()) {
            if (shield.isExpired()) continue;

            // Afstand tot schild
            if (attackerLoc.distanceSquared(shield.getCenter()) > 16) continue; // 4 blokken radius

            // Check of aanval kruist met schildvlak
            boolean blocked = UltraShieldExtension.blocksLineOfAttack(shield, attackerLoc.toVector(), targetLoc.toVector());

            if (blocked) {
                event.setCancelled(true);
                attacker.getWorld().playSound(attacker.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1f);
                attacker.spawnParticle(Particle.CRIT, shield.getCenter(), 10, 0.5, 0.5, 0.5, 0.01);
                return;
            }
        }
    }

    @Override
    @EventHandler
    public void onUse(PlayerInteractEvent event) {

        if (event.getPlayer().getInventory().getItemInOffHand().getItemMeta() != null) {
            if (event.getPlayer().getInventory().getItemInOffHand().getItemMeta().hasCustomModelData()) {
                if (event.getPlayer().getInventory().getItemInOffHand().getItemMeta().getCustomModelData() == 732) {
                    handleUse(event.getPlayer(), null);
                    return;
                }
            }
        }

        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) {
                if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getCustomModelData() == 732) {
                    handleUse(event.getPlayer(), null);
                }
            }
        }
    }

    //@EventHandler
 //   public void onShieldBreak() {};

    public void handleUse(Player blocker, @Nullable EntityDamageByEntityEvent event) {
        final long[] cooldown = {0};
        id.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!blocker.isHandRaised()) {
                    id.forEach(i -> {
                        Bukkit.getScheduler().cancelTask(i);
                        System.out.println("canceling task with id %s".formatted(i));
                    });

                    return;
                }
                if (event != null) {
                    event.setCancelled(true);
                }
                blocker.setShieldBlockingDelay(0);
                if (cooldown[0] < System.currentTimeMillis()) {
                    System.out.println("yes we do this as well");
                    Location eye = blocker.getEyeLocation();
                    // Vector direction = eye.getDirection().normalize();
                    //Location location = eye.clone().add(direction);

                    UltraShieldAnimation animation = new UltraShieldAnimation(eye, 0L, 1L);
                    animation.setDuration(1L);
                    Vector direction = eye.clone().getDirection().normalize();
                    Location center = eye.clone().add(direction);

                    UltraShieldExtension.UltraShieldData data = new UltraShieldExtension.UltraShieldData(center, direction, animation.getDuration());
                    if (UltraShieldExtension.getActiveShields().get(blocker) != null) {
                        UltraShieldExtension.UltraShieldData usd = UltraShieldExtension.getActiveShields().get(blocker);
                        usd.setCenter(center);
                        usd.setDirection(direction);
                        usd.setExpirationTime(animation.getDuration());
                    } else {
                        UltraShieldExtension.getActiveShields().put(blocker, data);
                    }
                    animation.play(0L, 1L);
                    cooldown[0] = System.currentTimeMillis() + 400L;
                }
            }
        }, 0L, 2L));
    }

}
