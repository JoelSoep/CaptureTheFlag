package me.joelsoep.ctf.cosmetic.type.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessageCosmeticListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = player.getKiller();
        Game game = Main.getGame();

        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            return;
        }

        if (killer == null) {
            return;
        }

        CTFPlayer ctfKiller = PlayerService.getPlayer(killer.getName());
        if (ctfKiller == null) {
            return;
        }

        Cosmetic cosmetic = ctfKiller.getCosmetics().getAppliedCosmetic(CosmeticType.DEATH_MESSAGE);
        if (cosmetic == null) {
            return;
        }

        if (!(cosmetic instanceof DeathMessageCosmetic deathMessageCosmetic)) {
            return;
        }

        Component message = MM.deserialize(deathMessageCosmetic.getDeathMessage().replace("%k", ctfKiller.getTeam().getTeamColor().getColor() +  killer.getName()).replace("%p", ctfPlayer.getTeam().getTeamColor().getColor() + player.getName()));
        game.getAudience().sendMessage(message);
    }
}
