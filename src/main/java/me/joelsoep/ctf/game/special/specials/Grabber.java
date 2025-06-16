package me.joelsoep.ctf.game.special.specials;

import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.game.special.Reflective;
import me.joelsoep.ctf.util.MM;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

 public class Grabber /** extends BaseSpecial<EntityShootBowEvent> implements Reflective **/ {
/**
    private final ArrayList<Location> particlesList;

    public Grabber() {
        super("grabber");

        particlesList = new ArrayList<>();
    }

    @Override
    protected ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
       // item.addEnchantment(Enchantment.DURABILITY, 1);
        meta.setCustomModelData(77);
        meta.displayName(MM.deserialize("<aqua>ɢʀᴀʙʙᴇʀ"));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    @EventHandler
    public void onUse(EntityShootBowEvent event) {

    }

    @Override
    public void reflect(Event event, @NotNull Player player, @NotNull Player reflector) {

    }

    private void summonParticle(Location location) {
        Particle particle = Particle.DUST_COLOR_TRANSITION;
        World world = location.getWorld();
        world.spawnParticle(particle, location, 5, new Particle.DustOptions(Color.fromRGB(245, 42, 214), 1));
    }**/
}
