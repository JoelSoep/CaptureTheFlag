package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.kit.BaseKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = Main.playerList.get(player.getName());

        if (ctfPlayer == null) {
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
