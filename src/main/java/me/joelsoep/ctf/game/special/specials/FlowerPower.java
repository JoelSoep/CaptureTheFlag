package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlowerPower extends BaseSpecial<PlayerInteractEvent> {
    private static final int EFFECT_DURATION = 7 * 20;
    private static final int EFFECT_AMPLIFIER = 2;

    public FlowerPower() {
        super("FlowerPower");
    }

    @Override
    public ItemStack setItemStack() {
        ItemStack item = new ItemStack(Material.WHITE_TULIP);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MM.deserialize("<aqua>Flower Power"));
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

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, EFFECT_DURATION, EFFECT_AMPLIFIER, true, false, false));

        if (itemStack == null) {
            return;
        }

        if (itemStack.getAmount() == 1) {
            player.getInventory().remove(itemStack);
        } else {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
    }
}
