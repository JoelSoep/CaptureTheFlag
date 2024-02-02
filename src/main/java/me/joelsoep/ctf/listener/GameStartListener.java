package me.joelsoep.ctf.listener;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.GameStartEvent;
import me.joelsoep.ctf.game.task.CountdownTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.game.team.TeamService;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class GameStartListener implements Listener {

    @EventHandler
    public void onStart(GameStartEvent event) {
        Game game = event.getGame();
        ArrayList<CTFPlayer> players = game.getPlayers();

        Team red = game.getRed();
        Team blue = game.getBlue();

        game.setState(GameState.READY);
        LogModule.GAME.log("Updated game state to READY");
        game.setStarting(false);
        game.setSpedUp(false);
        TeamService.setTeams(game);

        ArrayList<CTFPlayer> redPlayers = game.getRed().getPlayers();
        ArrayList<CTFPlayer> bluePlayers = game.getBlue().getPlayers();

        redPlayers.forEach(player -> {
            player.getPlayer().teleport(game.getRed().getSpawn());
        });

        bluePlayers.forEach(player -> {
            player.getPlayer().teleport(game.getBlue().getSpawn());
        });

        game.getSpectators().forEach(spectator -> {
            spectator.getPlayer().setGameMode(GameMode.SPECTATOR);
            spectator.getPlayer().teleport(game.getMap().getCenterLocation());
        });

            red.getPlayers().forEach(player -> {
                player.getPlayer().displayName(
                        MM.deserialize(
                                red.getTeamColor().getPrefix())
                                .append(MM.deserialize(" <gray>" + player.getPlayer().getName()))
                );
                player.getPlayer().playerListName(
                        MM.deserialize(
                                red.getTeamColor().getPrefix())
                                .append(MM.deserialize(" <gray>" + player.getPlayer().getName() + " ✪" + player.getLevel()))
                );
            });

            blue.getPlayers().forEach(player -> {
                player.getPlayer().displayName(
                        MM.deserialize(
                                        blue.getTeamColor().getPrefix())
                                .append(MM.deserialize(" <gray>" + player.getPlayer().getName()))
                );
                player.getPlayer().playerListName(
                        MM.deserialize(
                                        blue.getTeamColor().getPrefix())
                                .append(MM.deserialize(" <gray>" + player.getPlayer().getName() + " ✪" + player.getLevel()))
                );
        });

            // Game starts: some data is pushed to the database, like the initial players.

        List<String> names = List.of(game.getPlayers().stream().map(CTFPlayer::getPlayer).map(Player::getName).toArray(String[]::new));

        Main.getMongoManager().insertOne(Main.getMongoManager().getCollection("game"), new Document("_id", game.getUuid().toString())
                .append("players", names)
        );

        players.forEach(player -> {
            player.getPlayer().closeInventory();
            if (player.getKit() == null) {
                player.setKit(Main.getKitService().getAllKits().get(0));
            }

            Main.getKitService().giveKitToPlayer(player.getPlayer(), player.getKit().getName());
        });

        new CountdownTask(
                MM.deserialize("<#63f02b>Fight!"),
                game.getAudience(),
                7,
                true,
                game
        );
    }
}