package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.event.PlayerRewardSpecialEvent;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PlayerRewardSpecialListener implements Listener {

    @EventHandler
    public void onReward(PlayerRewardSpecialEvent event) {
        CTFPlayer player = event.getPlayer();
        BaseSpecial<?> special = Main.specialService.getRandomSpecial();
        ItemStack itemStack = special.getItemStack();

        if (itemStack == null) {
            player.sendMessage(MM.deserialize("""
        <red>An error occured while trying to give you your special.
        <red>Please report the following error to an administrator: BASESPECIAL_LISTENER_STACK_IS_NULL
        """));
            return;
        }
        player.getPlayer().getInventory().addItem(itemStack);

        player.sendMessage(MM.deserialize("<gray>You have received the %s" + "<gray>!").replaceText(TextReplacementConfig.builder().match("%s").replacement(itemStack.getItemMeta().displayName()).build()));
    }
}
