package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.game.special.Reflective;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

public class FireworkRider extends BaseSpecial<EntityDamageByEntityEvent> implements Reflective<EntityDamageByEntityEvent> {
    private static final int FIREWORK_POWER = 2;
    public FireworkRider() {
        super("FireworkRider");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemBuilder(Material.FIREWORK_STAR)
                .setName(MM.deserialize("<red>Fermented Firework"))
                .addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ATTRIBUTES)
                .build();
    }

    @Override
    @EventHandler
    public void onUse(EntityDamageByEntityEvent event) {
        if (!isEqual(event, getItemStack())) {
            return;
        }
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }

        if (!(event.getEntity() instanceof  Player player)) {
            return;
        }

        if (isReflective(player)) {
            reflect(event, damager, player);
            damager.getInventory().getItemInMainHand().subtract();
            return;
        }
        spawnFirework(player).addPassenger(player);
        damager.getInventory().getItemInMainHand().subtract();
    }

    @Override
    public void reflect(EntityDamageByEntityEvent event, @NotNull Player player, @NotNull Player reflector) {
        Mirror.playUseSound(player, reflector);
        spawnFirework(player).addPassenger(player);
        Mirror.sendReflectorMessage(player, this, reflector);
        Mirror.sendReflectedMessage(reflector, this, player);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getDismounted() instanceof Firework firework)) {
            return;
        }
        Component name = firework.customName();
        if (name == null) {
            return;
        }

        if (name.equals(MM.deserialize("firework_rider"))) {
            event.setCancelled(true);
        }
    }

    public Firework spawnFirework(Player rider) {
        Firework firework = (Firework) rider.getWorld().spawnEntity(rider.getEyeLocation().toBlockLocation(), EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(FIREWORK_POWER);
        meta.addEffect(FireworkEffect.builder()
                .withFade(Color.YELLOW)
                .withTrail()
                .withColor(Color.FUCHSIA)
                .with(FireworkEffect.Type.BURST)
                .build());
        firework.setFireworkMeta(meta);
        firework.customName(MM.deserialize("firework_rider"));
        return firework;
    }
}
