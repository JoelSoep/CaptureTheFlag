package me.joelsoep.ctf.game.kit.kits;

import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Archer extends BaseKit {

    public Archer() {
        super("Archer", Material.BOW, MM.deserialize("<#fc0324>ᴀʀᴄʜᴇʀ ᴋɪᴛ"));
    }

    @Override
    public void setArmor() {
        armor[0] = new ItemStack(Material.CHAINMAIL_BOOTS);
        armor[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        armor[2] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        armor[3] = new ItemStack(Material.CHAINMAIL_HELMET);
    }

    @Override
    public void setItems() {
        items.put(0, new ItemStack(Material.GOLDEN_AXE));
        items.put(1, new ItemStack(Material.WOODEN_SWORD));
        items.put(2, new ItemStack(Material.BOW));
        items.put(3, getDefaultPickaxe());
        items.put(4, new ItemStack(Material.GRAY_CONCRETE));
        items.put(7, new ItemStack(Material.ARROW, 32));
        items.put(8, new ItemStack(Material.GOLDEN_APPLE));
    }

    @Override
    public void setHand() {
        hand[0] = new ItemStack(Material.CROSSBOW);
    }
}
