package me.joelsoep.ctf.util.deprecated;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * This is a test item. It's not meant for use in production.
 */
@Deprecated
public class SmashBros extends BaseSpecial<EntityDamageByEntityEvent> {

    private Player attacker;

    public SmashBros() {
        super("SmashBros");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES);
        meta.displayName(MM.deserialize("<gold>Smash Bros"));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    @EventHandler
    public void onUse(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player player)) {
            return;
        }

        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        CTFPlayer ctfAttacker = PlayerService.getPlayers().get(attacker.getName());

        if (ctfPlayer == null || ctfAttacker == null) {
            return;
        }

        if (ctfPlayer.getTeam() == null || ctfAttacker.getTeam() == null) {
            return;
        }

        int slot = attacker.getInventory().getHeldItemSlot();
        if (attacker.getInventory().getItem(slot) == null) {
            return;
        }
        ItemStack item = attacker.getInventory().getItem(slot);
        this.attacker = attacker;

        if (item == null) {
            return;
        }

        if (item.getItemMeta().displayName() == null) {
            return;
        }

        if (item.getType() == getItemStack().getType() && item.hasItemFlag(ItemFlag.HIDE_DYE)) {
            ItemStack offHand = player.getInventory().getItemInOffHand();
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack possibleMirror = null;
            if (mainHand.getType() == Material.END_CRYSTAL && mainHand.hasItemFlag(ItemFlag.HIDE_DYE)) {
                possibleMirror = mainHand;
                slot = player.getInventory().getHeldItemSlot();
            }

            if (offHand.getType() == Material.END_CRYSTAL && offHand.hasItemFlag(ItemFlag.HIDE_DYE)) {
                possibleMirror = offHand;
                slot = -1;
            }

            if (possibleMirror != null) {
                ItemStack itemStack = possibleMirror;
                int currentAmount = itemStack.getAmount() ;
                if (currentAmount == 0) {
                    itemStack = null;
                } else {
                    itemStack.setAmount(currentAmount - 1);
                }
                if (slot == -1) {
                    player.getInventory().setItemInOffHand(itemStack);
                } else {
                    player.getInventory().setItem(slot, itemStack);
                }
                attacker.setVelocity(new Vector(0, 5, 0).multiply(20));
                attacker.setKiller(player);
                player.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.MASTER, 10, (float) 1.1));
                attacker.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.MASTER, 10, (float) 1.1));
                Main.getGame().getAudience().playSound(Sound.sound(Key.key("entity.firework_rocket.launch"), Sound.Source.MASTER, 10, (float) 1.08));
                Main.getGame().getAudience().sendMessage(MM.deserialize("<gray>%s <gray>mirrored %s<gray>'s Smash Attack!".formatted(ctfPlayer.getTeam().getTeamColor().getColor() + player.getName(), ctfAttacker.getTeam().getTeamColor().getColor() + attacker.getName())));
                task(attacker);
                yoink(attacker, player);
                event.setCancelled(true);
                return;
            }
            if (slot == -1) {
                attacker.getInventory().setItemInOffHand(null);
            } else {
                attacker.getInventory().setItem(slot, null);
            }
            player.setVelocity(new Vector(0, 5, 0).multiply(20));
            player.setKiller(attacker);
            Main.getGame().getAudience().playSound(Sound.sound(Key.key("entity.firework_rocket.launch"), Sound.Source.MASTER, 10, (float) 1.08));
            task(player);
            yoink(player, attacker);
            attacker.setHealth(4);
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 7, 1, true, false, true));
            event.setCancelled(true);
        }

    }

    private void yoink(Player player, Player attacker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CapturetheFlag");
        if (plugin == null) {
            Bukkit.getLogger().log(Level.SEVERE, "plugin is null @SmashBros-TASK");
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.setVelocity(new Vector(0, 0, 10).multiply(50));
                boom(player, attacker);
            }
        }, 20L);
    }

    public void boom(Player player, Player attacker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CapturetheFlag");
        if (plugin == null) {
            Bukkit.getLogger().log(Level.SEVERE, "plugin is null @SmashBros-TASK");
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.getWorld().createExplosion(player.getLocation(), 0, false, false, attacker);
                player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.DRAGON_BREATH, 0));
                check(player, attacker);

            }
        }, 20L);
    }

    private void task(Player player) {
        final int[] i = {0};
        int[] id = {0};
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CapturetheFlag");
        if (plugin == null) {
            Bukkit.getLogger().log(Level.SEVERE, "plugin is null @SmashBros-TASK");
            return;
        }
        id[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                i[0]++;
                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 20);
                if (i[0] == 30) {
                    cancel(id[0]);
                }
            }
        }, 1L, 1L);
    }

    private void check(Player player, Player attacker) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CapturetheFlag");
        if (plugin == null) {
            Bukkit.getLogger().log(Level.SEVERE, "plugin is null @SmashBros-TASK");
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (player.getGameMode() != GameMode.SPECTATOR) {
                   // player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.DRAGON_BREATH, 0));
                  //  PlayerDeathEvent deathEvent =new PlayerDeathEvent(player, Arrays.stream(player.getInventory().getContents()).toList(), 0, Component.empty());
                   // Bukkit.getPluginManager().callEvent(deathEvent);
                }
            }
        }, 5L);
    }

    private void cancel(int id) {
        if (id != 0) {
            Bukkit.getScheduler().cancelTask(id);
        }
    }
}
