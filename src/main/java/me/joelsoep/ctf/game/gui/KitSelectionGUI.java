package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.game.kit.KitService;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KitSelectionGUI extends ChestGui implements Listener {

    private final KitService kitService;
    private final OutlinePane pane;
    private final PatternPane background;

    public KitSelectionGUI(Player player, KitService kitService) {
        super(3, "Kit Selection");
        this.kitService = kitService;
        this.pane = new OutlinePane(2, 1, 5, 1);
        this.background = new PatternPane(0, 0, 9, 3, new Pattern(
                "111111111",
                "110000011",
                "111111111"
        ));

        this.background.setPriority(Pane.Priority.HIGH);
        this.background.bindItem('1', new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        this.pane.setPriority(Pane.Priority.HIGHEST);
        this.addPane(pane);
        this.addPane(background);

        openKitSelectionGUI(player);
    }

    /**
     * Creates the GUI by looping through all existing kits.
     * These kits are then displayed with an item (kit.icon()) and a name.
     *
     * If the player has a kit enabled, the enabled kit will glow.
     *
     * @param player the player to display the GUI for.
     */

    private void openKitSelectionGUI(Player player) {
        List<BaseKit> kits = kitService.getAllKits();
        this.pane.getItems().clear();
        int unused = 7;

        List<Component> lore = List.of(
                MM.deserialize("<red>Left-Click to Select"),
                MM.deserialize("<green>Right-Click to Sort / View Kit")
        );

        // Create GUI items for each kit
        for (int i = 0; i < kits.size(); i++) {
            BaseKit kit = kits.get(i);
            ItemStack kitItem = new ItemStack(kit.getIcon());
            ItemMeta kitMeta = kitItem.getItemMeta();
            kitMeta.displayName(kit.getStyledName());
            kitMeta.lore(lore);
            kitMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            kitItem.setItemMeta(kitMeta);

            // If the kit is enabled, make the item glow
            if (isKitEnabled(player, kit)) {
                kitItem.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            }

            this.setOnGlobalClick(event -> { event.setCancelled(true); });

            GuiItem guiItem = new GuiItem(kitItem, event -> {
                ClickType clickType = event.getClick();
                if (clickType == ClickType.LEFT) {
                    handleKitSelection(player, kit);
                }
                if (clickType == ClickType.RIGHT) {
                    new KitSortGUI(player, kit);
                }
                event.setCancelled(true);
            });
            this.pane.addItem(guiItem);
            unused--;
        }

        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MM.deserialize("<red>Coming soon!"));
        itemStack.setItemMeta(meta);

        GuiItem guiItem = new GuiItem(itemStack);

        for (int i = 0; i < unused; i++) {
            this.pane.addItem(guiItem);
        }
        this.update();
        this.show(player);
    }

    /**
     * Confirms if a player has a specific kit equipped.
     *
     * @param player The player whose kit needs to be checked.
     * @param kit The kit that the player should have.
     * @return a boolean to react upon.
     */

    private boolean isKitEnabled(Player player, BaseKit kit) {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        return ctfPlayer != null && ctfPlayer.getKit() == kit;
    }

    /**
     * Checks if the player has the kit enabled or disabled. It reacts accordingly by disabling or enabling the kit.
     * Afterwards, a new instance of the GUI is created in order to update the kit.
     *
     * @param player The player that gets to see the updated GUI.
     * @param kit The kit to select / deselect.
     */

    private void handleKitSelection(Player player, BaseKit kit) {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        if (ctfPlayer.getKit() == kit) {
            ctfPlayer.setKit(null);
            player.sendMessage(MM.deserialize("<#f2180c>Deselected ").append(kit.getStyledName()));
        } else {
            ctfPlayer.setKit(kit);
            player.sendMessage(MM.deserialize("<#91e344>Selected ").append(kit.getStyledName()));
        }
        new KitSelectionGUI(player, Main.kitService);
    }
}