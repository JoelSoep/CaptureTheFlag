package me.joelsoep.ctf.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class Hotbar {

    private final Player player;

    public Hotbar(Player player) {
        this.player = player;
        player.getInventory().clear();
        giveKitSelectionItem();
        giveAdminMenu();
    }

    private void giveKitSelectionItem() {
        ItemStack kitSelector = new ItemStack(Material.BOOK);
        ItemMeta itemMeta = kitSelector.getItemMeta();

        // Create a MiniMessage Component for the display name and parse colors
        Component displayName = MM.deserialize("<green>Select Kit</green>");
        itemMeta.displayName(displayName);

        // Create a MiniMessage Component for the lore and parse colors
        Component lore = MM.deserialize("<gray>Right-click to choose a kit.</gray>");
        itemMeta.lore(Collections.singletonList(lore));

        kitSelector.setItemMeta(itemMeta);
        this.player.getInventory().setItem(4, kitSelector);
    }

    /***
     * Deprecated.
     * Please use the new Queue block instead over the Participation menu,
     * as the Participation menu is scheduled for removal.
     */
    
    @Deprecated
    private void giveParticipationMenuItem() {
        ItemStack participationMenu = new ItemStack(Material.ENDER_EYE);
        ItemMeta itemMeta = participationMenu.getItemMeta();

        Component displayName = MM.deserialize("<gold>Participation Menu</gold>");
        itemMeta.displayName(displayName);

        Component lore = MM.deserialize("<gray>Right-click to set your participation availability");
        itemMeta.lore(Collections.singletonList(lore));

        participationMenu.setItemMeta(itemMeta);
        this.player.getInventory().setItem(6, participationMenu);
    }

    private void giveAdminMenu() {
        if (!player.hasPermission("ctf.admin")) {
            return;
        }

        ItemStack adminPanel = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = adminPanel.getItemMeta();

        Component displayName = MM.deserialize("<red>Admin Panel</red>");
        meta.displayName(displayName);

        Component lore = MM.deserialize("<gray>Right-click to open the admin panel!");
        meta.lore(Collections.singletonList(lore));

        meta.addItemFlags(ItemFlag.HIDE_DYE);
        adminPanel.setItemMeta(meta);
        this.player.getInventory().setItem(8, adminPanel);
    }
}
