package me.joelsoep.ctf.game.special;

import me.joelsoep.ctf.game.special.specials.Mirror;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BaseSpecial<T extends Event> implements Listener {
    protected String name;
    private final ItemStack itemStack;

    public BaseSpecial(String name) {
        this.name = name;
        this.itemStack = getItemStack();
        itemStack.addItemFlags(ItemFlag.HIDE_DYE);
    }

    public ItemStack getItem() {
        return itemStack;
    }

    protected abstract ItemStack getItemStack();

    @EventHandler
    public abstract void onUse(T event);

    public String getName() {
        return this.name;
    }

    public void giveSpecialToPlayer(CTFPlayer player) {
        if (getItemStack() == null) {
            player.sendMessage(MM.deserialize("""
                    <red>An error occurred while trying to give you your special.
                    <red>Please report the following error code to an administrator: BASESPECIAL_STACK_IS_NULL
                    """));
            return;
        }
        player.getPlayer().getInventory().addItem(this.getItemStack());
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

    protected boolean isEqual(EntityDamageByEntityEvent event, ItemStack stack) {
        if (!(event.getDamager() instanceof Player damager)) {
            return false;
        }
        ItemStack itemStack = damager.getInventory().getItemInMainHand();
        if (itemStack.getType() != stack.getType() || !itemStack.hasItemFlag(ItemFlag.HIDE_DYE)) {
            return false;
        }
        return true;
    }

    protected boolean isReflective(Player player) {
        if (!Reflective.class.isAssignableFrom(this.getClass())) {
            LogModule.SPECIAL.log("NOT REFLECTIVE CUZ CLASS");
            return false;
        }
        PlayerInventory inventory = player.getInventory();

        if (inventory.getItemInMainHand().getType() == Material.END_CRYSTAL) {
            if (inventory.getItemInMainHand().hasItemFlag(ItemFlag.HIDE_DYE)) {
                decrementItem(player, inventory.getItemInMainHand());
                return true;
            }
        }

        if (inventory.getItemInOffHand().getType() == Material.END_CRYSTAL) {
            if (inventory.getItemInOffHand().hasItemFlag(ItemFlag.HIDE_DYE)) {
                decrementItem(player, inventory.getItemInOffHand());
                return true;
            }
        }
        return false;
    }

    protected void decrementItem(Player player, ItemStack itemStack) {
        if (itemStack.getAmount() == 1) {
            player.getInventory().remove(itemStack);
        } else {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
    }
}
