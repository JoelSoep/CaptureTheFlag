package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.event.DummyEvent;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Mirror extends BaseSpecial<DummyEvent> {
    public Mirror() {
        super("Mirror");
    }

    @Override
    public ItemStack setItemStack() {
        ItemStack item = new ItemStack(Material.END_CRYSTAL);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.displayName(MM.deserialize("<dark_purple>Mirror"));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * This method is not registered as a Listener, as this item is purely a placeholder.
     * An inventory check should be applied in case a mirror affects its ability.
     *
     * @param event DummyEvent as a placeholder.
     */

    @Override
    @EventHandler
    public void onUse(DummyEvent event) {}
}