package me.joelsoep.ctf.game.kit.kits;

import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Warrior extends BaseKit {

    public Warrior() {
        super("Warrior", Material.IRON_AXE, MM.deserialize("<#3884ff>ᴡᴀʀʀɪᴏʀ ᴋɪᴛ"));
    }

    @Override
    public void setArmor() {
        armor[0] = new ItemStack(Material.CHAINMAIL_BOOTS);
        armor[1] = new ItemStack(Material.IRON_LEGGINGS);
        armor[2] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        armor[3] = new ItemStack(Material.IRON_HELMET);
    }

    @Override
    public void setItems() {
        items.put(7, new ItemStack(Material.GOLDEN_APPLE, 1));
        items.put(0, new ItemStack(Material.IRON_AXE));
        items.put(1, getDefaultPickaxe());
        items.put(2, new ItemStack(Material.GRAY_CONCRETE));
        items.put(8, new ItemStack(Material.CROSSBOW));
        items.put(6, new ItemStack(Material.ARROW, 5));
    }

    @Override
    public void setHand() {}
}
