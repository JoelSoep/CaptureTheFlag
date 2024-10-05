package me.joelsoep.ctf.util.deprecated;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Deprecated
public class Warrior {

    private MiniMessage miniMessage;

    private final ItemStack helmet = new ItemStack(Material.IRON_HELMET);
    private final ItemStack chestPlate = new ItemStack(Material.IRON_CHESTPLATE);
    private final ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
    private final ItemStack boots = new ItemStack(Material.IRON_BOOTS);

    private final ItemStack sword = new ItemStack(Material.IRON_SWORD);

    private final ItemStack gapples = new ItemStack(Material.GOLDEN_APPLE, 2);

    private void modifyWearables() {

        ItemMeta helmetMeta = helmet.getItemMeta();
        ItemMeta chestPlateMeta = chestPlate.getItemMeta();
        ItemMeta leggingsMeta = leggings.getItemMeta();
        ItemMeta bootsMeta = boots.getItemMeta();

        helmetMeta.addEnchant(Enchantment.PROTECTION, 2, false);
        helmetMeta.setUnbreakable(true);
        helmetMeta.displayName(miniMessage.deserialize("<#3884ff>ᴡᴀʀʀɪᴏʀ ʜᴇʟᴍᴇᴛ"));
        helmet.setItemMeta(helmetMeta);

        chestPlateMeta.addEnchant(Enchantment.PROTECTION, 2, false);
        chestPlateMeta.setUnbreakable(true);
        chestPlateMeta.displayName(miniMessage.deserialize("<#3884ff>ᴡᴀʀʀɪᴏʀ ᴄʜᴇѕᴛᴘʟᴀᴛᴇ"));
        chestPlate.setItemMeta(chestPlateMeta);

        leggingsMeta.addEnchant(Enchantment.PROTECTION, 2, false);
        leggingsMeta.setUnbreakable(true);
        leggingsMeta.displayName(miniMessage.deserialize("<#3884ff>ᴡᴀʀʀɪᴏʀ ʟᴇɢɢɪɴɢѕ"));
        leggings.setItemMeta(leggingsMeta);

        bootsMeta.addEnchant(Enchantment.PROTECTION, 2, false);
        bootsMeta.setUnbreakable(true);
        bootsMeta.displayName(miniMessage.deserialize("<#3884ff>ᴡᴀʀʀɪᴏʀ ʙᴏᴏᴛѕ"));
        boots.setItemMeta(bootsMeta);
    }

    private void modifyUtils() {
        ItemMeta swordMeta = sword.getItemMeta();

        swordMeta.addEnchant(Enchantment.SHARPNESS, 2,false);
        swordMeta.addEnchant(Enchantment.SWEEPING_EDGE, 2, false);
        swordMeta.setUnbreakable(true);
        swordMeta.displayName(miniMessage.deserialize("<#3884ff>ᴡᴀʀʀɪᴏʀ ѕᴡᴏʀᴅ"));
        sword.setItemMeta(swordMeta);
    }
}
