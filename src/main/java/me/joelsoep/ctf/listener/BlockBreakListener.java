package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>Couldn't break block because your profile could not be fetched."));
            event.setCancelled(true);
            return;
        }

        if (ctfPlayer.isElevated()) {
            event.setCancelled(true);
            return;
        }

        if (ctfPlayer.getTeam() == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() == Material.BLUE_CONCRETE || event.getBlock().getType() == Material.RED_CONCRETE) {
            event.setDropItems(false);
        }
    }
}
