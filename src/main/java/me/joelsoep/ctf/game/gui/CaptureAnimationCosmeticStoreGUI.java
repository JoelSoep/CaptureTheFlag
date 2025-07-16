package me.joelsoep.ctf.game.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CaptureAnimationCosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.store.StoreCosmeticItem;
import me.joelsoep.ctf.store.StoreManager;
import me.joelsoep.ctf.util.GuiUtils;
import me.joelsoep.ctf.util.ItemBuilder;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.SoundUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CaptureAnimationCosmeticStoreGUI extends ChestGui {
    private final CTFPlayer player;
    private final OutlinePane itemPane;

    public CaptureAnimationCosmeticStoreGUI(CTFPlayer player) {
        super(4, "Death Message Store");
        this.player = player;

        this.itemPane = new OutlinePane(1, 1, 7, 2);
        GuiUtils.setBackground(this, new Pattern(
                "111111111",
                "100000001",
                "100000001",
                "111111111"
        ), 4);
        setItems();
        this.addPane(itemPane);
        this.show(player.getPlayer());
    }

    public void setItems() {
        @Nullable Cosmetic currentCosmetic = player.getCosmetics().getAppliedCosmetic(CosmeticType.CAPTURE_ANIMATION);

        StoreManager.getTypeItems(CosmeticType.CAPTURE_ANIMATION).forEach(item -> {
            CaptureAnimationCosmetic cosmetic = (CaptureAnimationCosmetic) Main.getCosmeticsManager().getCosmetic(CosmeticType.CAPTURE_ANIMATION, item.getName().split("\\.")[1]);
            if (cosmetic == null) {
                player.sendMessage(MM.announce("<red>Something went wrong while trying to apply this cosmetic. Please report this to an administrator."));
                player.playSound(SoundUtils.getDenySound());
                return;
            }

            ItemStack itemStack = new ItemBuilder(item.getDisplay())
                    .setName(MM.deserialize(item.getDisplayName()))
                    .setLore(
                            MM.deserialize("<!i><#FFFFFF>" + cosmetic.getRarity().getDisplayName()),
                            MM.deserialize("<#FFF800>⛁ " + (item.isFree() ? "FREE" : (int) (item.getPrice() * item.getDiscountCoefficient()))),
                            MM.deserialize(""),
                            MM.deserialize("<gray>Left-Click to purchase"),
                            MM.deserialize("<gray>Right-Click to preview")
                    )
                    .build();
            if (currentCosmetic != null) {
                if (item.getName().equals(currentCosmetic.getType().getPrefix() + currentCosmetic.getName())) {
                    itemStack.editMeta(meta -> meta.addEnchant(Enchantment.UNBREAKING, 1, false));
                }
            }
            GuiItem guiItem = new GuiItem(itemStack, (action -> {

                if (action.isLeftClick()) {
                    new StoreCosmeticItemVerificationGui(player, (StoreCosmeticItem) item);
                    player.getPlayer().playSound(SoundUtils.getClickSound());
                }

                if (action.isRightClick()) {
                 //   player.getPlayer().getOpenInventory().close();
                  //  player.sendMessage(MM.deserialize("<gray>Example message:"));
                  //  player.sendMessage(MM.deserialize(cosmetic.getDeathMessage().replace("%p", "JoelSoep").replace("%k", player.getPlayer().getName())));
                }
            }), Main.getInstance());
            itemPane.addItem(guiItem);
        });
    }
}
