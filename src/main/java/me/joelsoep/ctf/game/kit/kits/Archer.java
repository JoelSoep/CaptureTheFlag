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
        items.put(new ItemStack(Material.GOLDEN_AXE), 0);
        items.put(new ItemStack(Material.WOODEN_SWORD), 1);
        items.put(new ItemStack(Material.BOW), 2);
        items.put(getDefaultPickaxe(), 3);
        items.put(new ItemStack(Material.ARROW, 32), 7);
        items.put(new ItemStack(Material.GOLDEN_APPLE), 8);
    }

    @Override
    public void setHand() {
        hand[0] = new ItemStack(Material.CROSSBOW);
    }
}
