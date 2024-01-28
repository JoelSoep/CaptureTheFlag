package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ThunderRod extends BaseSpecial<PlayerInteractEvent> {
    private static final int MAX_DISTANCE = 55;

    public ThunderRod() {
        super("ThunderRod");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(MM.deserialize("<gold>Thunder Rod"));
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!this.isEqual(event, getItemStack())) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getTargetBlockExact(MAX_DISTANCE) == null) {
            player.sendActionBar(MM.deserialize("<red>Focus on a block to summon lightning!"));
            return;
        }

        Location location = Objects.requireNonNull(player.getTargetBlockExact(MAX_DISTANCE)).getLocation();
        Entity entity = player.getWorld().spawnEntity(location, EntityType.LIGHTNING);
        if (entity instanceof LightningStrike lightning) {
            lightning.setCausingPlayer(player);
        }

        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getAmount() == 1) {
            player.getInventory().remove(event.getItem());
        } else {
            event.getItem().setAmount(event.getItem().getAmount() - 1);
        }
    }
}
