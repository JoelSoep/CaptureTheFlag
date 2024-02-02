package me.joelsoep.ctf.util;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

import static me.joelsoep.ctf.util.Hotbar.Static.giveQueueBlock;

public class Hotbar {

    private final Player player;

    public Hotbar(Player player) {
        this.player = player;
        player.getInventory().clear();
        giveTutorialBook();
        giveKitSelectionItem();
        giveAdminMenu();
        giveQueueBlock(player);

        if (LocalConfig.ALLOW_CUSTOM_TEAMS) {
            giveTeamSelectionMenuItem();
        }
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

    private void giveTeamSelectionMenuItem() {
        ItemStack teamMenu = new ItemStack(Material.YELLOW_BANNER);
        ItemMeta itemMeta = teamMenu.getItemMeta();

        Component displayName = MM.deserialize("<blue>Team Selection</blue>");
        itemMeta.displayName(displayName);

        Component lore = MM.deserialize("<gray>Right-click to select a team!");
        itemMeta.lore(Collections.singletonList(lore));

        teamMenu.setItemMeta(itemMeta);
        this.player.getInventory().setItem(2, teamMenu);
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

    private void giveTutorialBook() {
        ItemStack tutorial = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = tutorial.getItemMeta();

        Component displayName = MM.deserialize("<#FFD700>Tutorial Book");
        meta.displayName(displayName);

        // no lore cuz the knowledge book errors

        meta.addItemFlags(ItemFlag.HIDE_DYE);
        tutorial.setItemMeta(meta);
        this.player.getInventory().setItem(0, tutorial);
    }

    public static class Static {

        public static void giveQueueBlock(Player player) {
            CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
            if (ctfPlayer == null) {
                return;
            }

           if (Main.getQueue().getQueue().contains(ctfPlayer) || Main.getGame().getPlayers().contains(ctfPlayer)) {
               player.getInventory().setItem(6, getEnqueuedItemStack());
           } else {
               player.getInventory().setItem(6, getDequeuedItemStack());
           }
        }

        public static ItemStack getEnqueuedItemStack() {
            ItemStack queueBlock = new ItemStack(Material.LIME_CONCRETE);
            ItemMeta itemMeta = queueBlock.getItemMeta();

            Component displayName = MM.deserialize("<green>You are queued");
            itemMeta.displayName(displayName);

            Component lore = MM.deserialize("<gray>Right-click to dequeue.");
            itemMeta.lore(Collections.singletonList(lore));

            queueBlock.setItemMeta(itemMeta);
            return queueBlock;
        }

        public static ItemStack getDequeuedItemStack() {
            ItemStack queueBlock = new ItemStack(Material.RED_CONCRETE);
            ItemMeta itemMeta = queueBlock.getItemMeta();

            Component displayName = MM.deserialize("<red>You are not in the queue.");
            itemMeta.displayName(displayName);

            Component lore = MM.deserialize("<gray>Right-click to enqueue.");
            itemMeta.lore(Collections.singletonList(lore));

            queueBlock.setItemMeta(itemMeta);
            return queueBlock;
        }
    }
}
