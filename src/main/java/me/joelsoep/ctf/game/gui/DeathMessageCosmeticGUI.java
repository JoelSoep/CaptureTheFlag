package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import lombok.extern.java.Log;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeathMessageCosmeticGUI extends ChestGui {
    private final Player player;
    private CTFPlayer ctfPlayer;
    private OutlinePane cosmetics;

    public DeathMessageCosmeticGUI(Player player) {
        super(4, "Death Messages");

        this.player = player;
        this.ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            LogModule.CORE.log("ctfPlayer is null @ DeathMessageCosmeticGUI");
            return;
        }

        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "100000001",
                "100000001",
                "111111111"
        ), 4);
        setCosmetics();
        this.show(player);
    }

    private void setCosmetics() {
        this.cosmetics = new OutlinePane(1, 1, 7, 2, Pane.Priority.HIGHEST);
        List<Cosmetic> deathMessages = Main.getCosmeticManager().getCosmetics(CosmeticType.DEATH_MESSAGE);
        if (deathMessages == null) {
            return;
        }

        deathMessages.forEach(cosmetic -> {
            if (!(cosmetic instanceof DeathMessageCosmetic)) {
                return;
            }

            Material logo = cosmetic.getLogo();
            Component color = MM.deserialize(cosmetic.getRarity().getColor());
            boolean enabled = ctfPlayer.getCosmetics().getAppliedCosmetic(CosmeticType.DEATH_MESSAGE) == cosmetic;
            Component displayName = cosmetic.getDisplayName();
            if (displayName == null) {
                displayName = MM.deserialize(cosmetic.getName());
            }

            ItemStack itemStack = new ItemBuilder(logo)
                    .setName(color.append(displayName))
                    .setLore(MM.deserialize("<gray>Click to equip"), MM.deserialize("<gray>Rarity: %s".formatted(cosmetic.getRarity().getDisplayName())))
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ENCHANTS)
                    .build();

            if (enabled) {
                itemStack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
            }

            final Component finalDisplayName = displayName;
            GuiItem item = new GuiItem(itemStack, event -> {
                if (enabled) {
                    ctfPlayer.getCosmetics().setAppliedCosmetic(CosmeticType.DEATH_MESSAGE, null);
                    player.sendMessage(MM.deserialize("<yellow>Removed cosmetic."));
                } else {
                    this.ctfPlayer.getCosmetics().setAppliedCosmetic(cosmetic.getType(), cosmetic);
                    player.sendMessage(MM.deserialize("<yellow>Applied ").append(finalDisplayName));
                }
                new DeathMessageCosmeticGUI(player);
            });
            this.cosmetics.addItem(item);
        });
        this.addPane(this.cosmetics);
    }
}
