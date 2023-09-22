package me.joelsoep.ctf.game.kit.kits;

import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class Knight extends BaseKit {
    public Knight() {
        super("Knight", Material.IRON_SWORD, MM.deserialize("<#ebd007>ᴋɴɪɢʜᴛ ᴋɪᴛ"));
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
        items.put(new ItemStack(Material.IRON_SWORD), 0);
        items.put(new ItemStack(Material.BOW), 1);
        items.put(new ItemStack(Material.ARROW, 5), 12);
        items.put(getDefaultPickaxe(), 2);
        items.put(new ItemStack(Material.GOLDEN_APPLE, 1), 8);
    }

    @Override
    public void setHand() {
        ItemStack item = new ItemStack(Material.SHIELD);
        //Damageable itemMeta = (Damageable) item.getItemMeta();
       // itemMeta.setDamage(333);
        //item.setItemMeta(itemMeta);
        hand[0] = item;
    }
}