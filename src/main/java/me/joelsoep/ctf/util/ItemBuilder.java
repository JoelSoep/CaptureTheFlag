package me.joelsoep.ctf.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * ItemStack builder class.
 * For the main part stolen from the mcmanhunt-core.
 * @author nathanG
 * @author Wising
 * @author Joël
 */

public class ItemBuilder {
    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder setLocalizedName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLocalizedName(name);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setType(Material type) {
        itemStack.setType(type);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(MM.deserialize(name));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setName(Component name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(name);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(Component... lines) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(List.of(lines));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(Component line) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(Collections.singletonList(line));
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setLore(List<Component> lines) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(lines);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setSkullOwner(UUID uuid) {
        if (itemStack.getType() != Material.PLAYER_HEAD)
            throw new IllegalStateException("Cannot set a skull owner on a non-player head");

        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        itemStack.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level, boolean hide) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        if (hide) {
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag... itemFlag) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlag);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        itemStack.editMeta(itemMeta -> itemMeta.setCustomModelData(customModelData));
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}
