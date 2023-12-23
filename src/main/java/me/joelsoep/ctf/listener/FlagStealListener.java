package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.event.FlagStealEvent;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class FlagStealListener implements Listener {

    @EventHandler
    public void onSteal(FlagStealEvent event) {
        Game game = event.getGame();
        CTFPlayer ctfPlayer = event.getStealer();
        Player player = ctfPlayer.getPlayer();
        @NotNull Team team = ctfPlayer.getTeam();
        Team flagOwner = event.getFlagOwner();

        if (team == null) {
            Bukkit.getLogger().log(Level.SEVERE, player.getName() + "\\'s team is null @ flag steal!");
            return;
        }

        ItemStack helmet = new ItemStack(flagOwner.getFlag().getMaterial());
        helmet.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        player.getInventory().setHelmet(helmet);
        game.getMap().removeFlag(flagOwner);
        flagOwner.getFlag().setThief(ctfPlayer);
        player.setGlowing(true);
        game.getAudience().sendMessage(MM.deserialize("""
                <gray>Team %s<gray>'s flag has been stolen by %s<gray>!"""
                .formatted(flagOwner.getTeamColor().getPrefix(), team.getTeamColor().getColor() + player.getName())));
    }
}
