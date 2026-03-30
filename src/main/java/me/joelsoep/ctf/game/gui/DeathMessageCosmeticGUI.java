package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
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
import me.joelsoep.ctf.util.SoundUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeathMessageCosmeticGUI extends ChestGui {
    private final Player player;
    private CTFPlayer ctfPlayer;
    private OutlinePane cosmetics;

    public DeathMessageCosmeticGUI(@NotNull Player player) {
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
        List<Cosmetic> deathMessages = Main.getCosmeticsManager().getCosmetics(CosmeticType.DEATH_MESSAGE);
        if (deathMessages == null) {
            return;
        }

        deathMessages.forEach(cosmetic -> {
            if (!(cosmetic instanceof DeathMessageCosmetic)) {
                return;
            }

            if (!ctfPlayer.ownsCosmetic(cosmetic)) {
                return;
            }

            Material logo = cosmetic.getLogo();
            Component color = MM.deserialize(cosmetic.getRarity().getColor());
            boolean enabled = ctfPlayer.getCosmetics().getAppliedCosmetic(CosmeticType.DEATH_MESSAGE) == cosmetic;
            Component displayName = cosmetic.getDisplayName();
            if (displayName == null) {
                displayName = MM.deserialize(cosmetic.getName());
            }

            List<Component> lines;
            if (cosmetic.getLore() == null) {
                lines = List.of(
                        MM.deserialize("<gray>Left-Click to equip"),
                        MM.deserialize("<gray>Shift-Left-Click to preview"),
                        MM.deserialize("<gray>Right-Click to change color")
                );
            } else {
                List<Component> list = new ArrayList<>(List.copyOf(cosmetic.getLore()));
                list.add(MM.deserialize(""));
                list.add(MM.deserialize("<gray>Left-Click to equip"));
                list.add(MM.deserialize("<gray>Shift-Left-Click to preview"));
                list.add(MM.deserialize("<gray>Right-Click to change color"));
                lines = list;
            }

            ItemStack itemStack = new ItemBuilder(logo)
                    .setName(color.append(displayName).append(MM.deserialize("  <#FFFFFF>" + cosmetic.getRarity().getDisplayName())))
                        .setLore(lines)
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ENCHANTS)
                    .build();

            if (enabled) {
                itemStack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
                itemStack.editMeta(meta -> meta.lore().add(MM.deserialize("")));
                itemStack.editMeta(meta -> meta.lore().add(MM.deserialize("<green>You've applied this item!")));
            }
            final Component finalDisplayName = displayName;
            GuiItem item = new GuiItem(itemStack, event -> {
                if (event.isLeftClick() && !event.isShiftClick()) {
                    handleLeftClick(cosmetic, finalDisplayName, enabled);
                }

                if (event.isLeftClick() && event.isShiftClick()) {
                    handleShiftLeftClick(cosmetic);
                }

                if (event.isRightClick()) {
                    handleRightClick(cosmetic);
                }
            });
            this.cosmetics.addItem(item);
        });
        this.addPane(this.cosmetics);
    }

    private void handleLeftClick(Cosmetic cosmetic, Component finalDisplayName, boolean enabled) {
        if (enabled) {
            ctfPlayer.getCosmetics().setAppliedCosmetic(CosmeticType.DEATH_MESSAGE, null);
            player.sendMessage(MM.announce("<yellow>Removed cosmetic."));
            player.playSound(SoundUtils.getConfirmSound());
        } else {
            this.ctfPlayer.getCosmetics().setAppliedCosmetic(cosmetic.getType(), cosmetic);
            player.playSound(SoundUtils.getConfirmSound());
            player.sendMessage(MM.announce("<yellow>Applied ").append(finalDisplayName));
        }
        new DeathMessageCosmeticGUI(player);
    }

    private void handleShiftLeftClick(Cosmetic cosmetic) {
        String message = ((DeathMessageCosmetic) cosmetic).getDeathMessage();
        player.getOpenInventory().close();
        player.sendMessage(MM.announce("<gray>Example message:"));
        player.sendMessage(MM.deserialize(message.replace("%p", "JoelSoep").replace("%k", player.getName())));
        player.closeInventory();
    }

    private void handleRightClick(Cosmetic cosmetic) {

    }
}
