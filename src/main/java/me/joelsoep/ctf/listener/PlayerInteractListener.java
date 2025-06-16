package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.gui.AdminPanelGUI;
import me.joelsoep.ctf.game.gui.CosmeticsGUI;
import me.joelsoep.ctf.game.gui.KitSelectionGUI;
import me.joelsoep.ctf.game.gui.TeamSelectionGUI;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.ParticipationState;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.TutorialBook;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * TODO: Make it so this class is not depending on the game state, but rather if a player is in a game or not.
 */

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Game game = Main.getGame();

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || game.getState() == GameState.WAITING && event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player player = event.getPlayer();
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        Material material = event.getMaterial();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        if (ctfPlayer == null) {
            return;
        }

        if (ctfPlayer.hasCooldown()) {
            return;
        }

        ctfPlayer.setCooldown(500L);

        if (material == Material.WRITTEN_BOOK && item.getItemMeta().getCustomModelData() == 3) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setCancelled(true);
            new TutorialBook(player);
            return;
        }

        if (material == Material.PAPER && item.getItemMeta().getCustomModelData() == 2047) {
            new KitSelectionGUI(player, Main.getKitService());
            return;
        }

        if (material == Material.YELLOW_BANNER) {
            if (game.getState() != GameState.WAITING) {
                player.sendMessage(MM.deserialize("<red>Can't open the team selector right now, please try once the current game is over."));
                return;
            }
            new TeamSelectionGUI(player);
            return;
        }

        if (material == Material.PAPER && item.getItemMeta().getCustomModelData() == 1012) {
            if (Main.getGameQueue().getQueue().contains(ctfPlayer)) {
                Main.getGameQueue().removePlayer(ctfPlayer);
            }

            if (Main.getGame().getPlayers().contains(ctfPlayer) && game.getState() == GameState.WAITING) {
                Main.getGame().removePlayer(ctfPlayer);
            }
            ctfPlayer.setParticipationState(ParticipationState.UNAVAILABLE);
            ctfPlayer.sendMessage("<gray>You left the queue.");
            Hotbar.Static.giveQueueBlock(player);
            return;
        }

        if (material == Material.PAPER && item.getItemMeta().getCustomModelData() == 1001) {
            new CosmeticsGUI(player);
            return;
        }

        if (material == Material.PAPER && item.getItemMeta().getCustomModelData() == 1011) {
            if (Main.getGameQueue().getQueue().contains(ctfPlayer)) {
                return;
            }

            if (Main.getGame().getPlayers().contains(ctfPlayer)) {
                return;
            }

            if (Main.getGame().getSpectators().contains(ctfPlayer)) {
                Main.getGame().removeSpectator(ctfPlayer);
                ctfPlayer.sendMessage(MM.deserialize("<gray>You stopped spectating."));
            }
            Main.getGameQueue().addPlayer(ctfPlayer);
            ctfPlayer.setParticipationState(ParticipationState.AVAILABLE);
            Hotbar.Static.giveQueueBlock(player);
            return;
        }

        if (event.getMaterial() == Material.PAPER && item.getItemMeta().getCustomModelData() == 1014) {
            if (!player.hasPermission("ctf.admin") && !player.isOp()) {
                player.sendMessage(MM.deserialize("<red>You do not have permission to use this!"));
                return;
            }
            if (!item.hasItemFlag(ItemFlag.HIDE_DYE)) {
                return;
            }
            new AdminPanelGUI(player);
        }
    }
}
