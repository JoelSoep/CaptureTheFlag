package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Leap extends BaseSpecial<PlayerInteractEvent> {

    public Leap() {
        super("Leap");
    }

    @Override
    public ItemStack setItemStack() {
        ItemStack item = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MM.deserialize("<yellow>Leap of Faith"));
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!this.isEqual(event, setItemStack())) {
            return;
        }

        ItemStack itemStack = event.getItem();
        Player player = event.getPlayer();

        player.setVelocity(player.getLocation().getDirection().multiply(2));
        if (itemStack.getAmount() == 1) {
            player.getInventory().remove(itemStack);
        } else {
            event.getItem().setAmount(itemStack.getAmount() - 1);
        }
    }
}
