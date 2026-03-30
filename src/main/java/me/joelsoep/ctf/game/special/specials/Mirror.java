package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.event.DummyEvent;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Mirror extends BaseSpecial<DummyEvent> {
    public Mirror() {
        super("Mirror");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.END_CRYSTAL);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.displayName(MM.deserialize("<dark_purple>Mirror"));
        item.setItemMeta(meta);
        item.lore(List.of(MM.deserialize("<dark_purple>Reflects player-specific items!"), MM.deserialize(""), MM.deserialize("<gray>Hold in your main- or offhand to use")));
        return item;
    }

    /**
     * This method is not registered as a Listener, as this item is purely a placeholder.
     * An inventory check should be applied in case a mirror affects its ability.
     *
     * @param event DummyEvent as a placeholder.
     */

    @Override
    @EventHandler
    public void onUse(DummyEvent event) {}

    public static void playUseSound(Audience... audiences) {
        Arrays.stream(audiences).forEach(audience -> {
            audience.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.MASTER, 10F, 0.9F));
        });
    }

    public static void sendReflectorMessage(Player reflected, @NotNull BaseSpecial<?> special, Audience... audiences) {
        if (special.getItem() == null) {
            return;
        }
        Component message = MM.deserialize("<green>You reflected <yellow>%s<green>'s adobus<green>!".formatted(
                reflected.getName()
        )).replaceText(TextReplacementConfig.builder().match("adobus").replacement(special.getItem().getItemMeta().displayName()).build());
        Arrays.stream(audiences).forEach(audience -> {
            audience.sendMessage(message);
        });
    }

    public static void sendReflectedMessage(Player reflector, @NotNull BaseSpecial<?> special, Audience... audiences) {
        if (special.getItem() == null) {
            return;
        }
        Component message = MM.deserialize("<green>Your <yellow>adobus<green> was reflected by %s<green>!".formatted(
                reflector.getName()
        )).replaceText(TextReplacementConfig.builder().match("adobus").replacement(special.getItem().getItemMeta().displayName()).build());
        Arrays.stream(audiences).forEach(audience -> {
            audience.sendMessage(message);
        });
    }
}