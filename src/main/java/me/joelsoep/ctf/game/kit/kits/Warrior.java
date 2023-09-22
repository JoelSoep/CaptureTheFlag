package me.joelsoep.ctf.game.kit.kits;

import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        items.put(new ItemStack(Material.GOLDEN_APPLE, 1), 7);
        items.put(new ItemStack(Material.IRON_AXE), 0);
        items.put(getDefaultPickaxe(), 1);
        items.put(new ItemStack(Material.CROSSBOW), 8);
        items.put(new ItemStack(Material.ARROW, 5), 6);
    }

    @Override
    public void setHand() {}
}
