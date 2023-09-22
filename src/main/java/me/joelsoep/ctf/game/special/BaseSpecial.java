package me.joelsoep.ctf.game.special;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public abstract class BaseSpecial<T extends Event> implements Listener {
    protected String name;
    private final ItemStack itemStack;

    public BaseSpecial(String name) {
        this.name = name;
        itemStack = this.setItemStack();
    }

    public abstract ItemStack setItemStack();

    @EventHandler
    public abstract void onUse(T event);

    public String getName() {
        return this.name;
    }

    public @Nullable ItemStack getItemStack() {
        return this.itemStack;
    }

    public void giveSpecialToPlayer(CTFPlayer player) {
        if (getItemStack() == null) {
            player.sendMessage(MM.deserialize("""
                    <red>An error occurred while trying to give you your special.
                    <red>Please report the following error code to an administrator: BASESPECIAL_STACK_IS_NULL
                    """));
            return;
        }
        player.getPlayer().getInventory().addItem(this.itemStack);
    }

    protected boolean isEqual(PlayerInteractEvent event, ItemStack stack) {
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        if (item == null) {
            return false;
        }

        if (item.getType() != stack.getType() || !item.hasItemFlag(ItemFlag.HIDE_DYE)) {
            return false;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return false;
        }
        return true;
    }
}
