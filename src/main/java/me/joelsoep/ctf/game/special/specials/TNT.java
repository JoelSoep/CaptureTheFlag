package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TNT extends BaseSpecial<PlayerInteractEvent> {
    private static final double TNT_VELOCITY_MULTIPLIER = 1.25;
    private static final int TNT_FUSE_TICKS = 55;

    public TNT() {
        super("TNT");
    }

    @Override
    public ItemStack setItemStack() {
        ItemStack itemStack = new ItemStack(Material.TNT);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(MM.deserialize("<red>Super TNT"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!this.isEqual(event, setItemStack())) {
            return;
        }

        Player player = event.getPlayer();
        @NotNull Vector vector = player.getLocation().getDirection();
        Entity tnt = player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);

        if (event.getItem().getAmount() == 1) {
            player.getInventory().remove(event.getItem());
        } else {
            event.getItem().setAmount(event.getItem().getAmount() - 1);
        }

        tnt.setVelocity(vector.multiply(TNT_VELOCITY_MULTIPLIER));
        ((TNTPrimed) tnt).setFuseTicks(TNT_FUSE_TICKS);
    }
}
