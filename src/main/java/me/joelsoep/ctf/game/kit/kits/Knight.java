package me.joelsoep.ctf.game.kit.kits;

import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class Knight extends BaseKit {
    public Knight() {
        super("Knight", "1.0.0", Material.IRON_SWORD, MM.deserialize("<#ebd007>ᴋɴɪɢʜᴛ ᴋɪᴛ"));
    }

    @Override
    public void setArmor() {
        armor[0] = new ItemStack(Material.IRON_BOOTS);
        armor[1] = new ItemStack(Material.IRON_LEGGINGS);
        armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
        armor[3] = new ItemStack(Material.IRON_HELMET);
    }

    @Override
    public void setItems() {
        items.put(0, new ItemStack(Material.IRON_SWORD));
        items.put(1, new ItemStack(Material.BOW));
        items.put(7, new ItemStack(Material.ARROW, 5));
        items.put(3, new ItemStack(Material.GRAY_CONCRETE));
        items.put(2, getDefaultPickaxe());
        items.put(8, new ItemStack(Material.GOLDEN_APPLE, 1));
    }

    @Override
    public void setHand() {
        ItemStack item = new ItemStack(Material.SHIELD);
        Damageable itemMeta = (Damageable) item.getItemMeta();
        itemMeta.setDamage(327);
        item.setItemMeta(itemMeta);
        hand[0] = item;
    }
}