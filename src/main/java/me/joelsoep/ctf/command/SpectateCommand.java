package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.ParticipationState;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("spectate|sp")
public class SpectateCommand extends BaseCommand {

    @CatchUnknown
    @Default
    @Description("Spectate the ongoing game")
    public void spectate(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>Something went wrong. Please contact an administrator."));
            LogModule.COMMAND.warning("the ctf-profile of player %s is null!".formatted(player.getName()));
            return;
        }

        if (ctfPlayer.getGame() != null) {
            player.sendMessage(MM.deserialize("<red>You cannot spectate right now because you are currently (queued) in a game!"));
            return;
        }

        if (ctfPlayer.getParticipationState() == ParticipationState.SPECTATING) {
            ctfPlayer.setParticipationState(ParticipationState.AVAILABLE);
            player.teleport(MapManager.getLobby().getSpawnLocation());
            player.sendMessage(MM.deserialize("<gray>You stopped spectating."));
            new Hotbar(player);
            return;
        }

        if (Main.getGame().getState() != GameState.READY && Main.getGame().getState() != GameState.RUNNING) {
            player.sendMessage(MM.deserialize("<gray>Toggled spectator mode. You will start spectating once the match begins."));
            ctfPlayer.setParticipationState(ParticipationState.SPECTATING);
            player.setGameMode(GameMode.ADVENTURE);
            Main.getGame().addSpectator(ctfPlayer);
            return;
        }

        player.teleport(Main.getGame().getMap().getCenterLocation());
        Main.getGame().addSpectator(ctfPlayer);
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(MM.deserialize("<gray>You are now spectating the current match. If you'd like to quit spectating, use /spectate again."));
        ctfPlayer.setParticipationState(ParticipationState.SPECTATING);
    }
}
