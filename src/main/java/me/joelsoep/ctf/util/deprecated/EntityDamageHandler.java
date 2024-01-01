package me.joelsoep.ctf.util.deprecated;

import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.UUID;

@Deprecated
public class EntityDamageHandler {

    private Plugin plugin = null;

    public EntityDamageHandler() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CapturetheFlag");
        if (plugin == null) {
            LogModule.UTILITY.severe("ERROR: Plugin CTF is null @EntityDamageHandler!!");
            LogModule.UTILITY.severe("ERROR: Plugin CTF is null @EntityDamageHandler!!");
            return;
        }
        this.plugin = plugin;
        LogModule.UTILITY.log("[CTF] Booted EntityDamageHandler!");
    }

    public void setLastDamager(Player player, @Nullable Player damager, EntityDamageEvent.DamageCause cause) {
        if (damager == null) {
            player.removeMetadata("lastDamager", plugin);
            return;
        }
        player.setMetadata("lastDamager", new FixedMetadataValue(plugin, damager.getUniqueId() + ":" + cause.toString()));
    }

    public Player getLastDamager(Player player) {
        if (player.hasMetadata("lastDamager")) {
            String damagerString = player.getMetadata("lastDamager").get(0).asString();
            String uuidString = damagerString.split(":")[0];
            UUID uuid = UUID.fromString(uuidString);
            return Bukkit.getPlayer(uuid);
        }
        return null;
    }

    public EntityDamageEvent.DamageCause getLastDamageCause(Player player) {
        if (player.hasMetadata("lastDamager")) {
            String damagerString = player.getMetadata("lastDamager").get(0).asString();
            String causeString = damagerString.substring(damagerString.lastIndexOf(":") + 1);
            return EntityDamageEvent.DamageCause.valueOf(causeString);
        }
        return null;
    }
}
