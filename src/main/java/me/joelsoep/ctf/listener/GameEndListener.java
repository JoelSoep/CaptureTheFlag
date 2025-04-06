package me.joelsoep.ctf.listener;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.task.CongratulationEffectTask;
import me.joelsoep.ctf.game.task.ResetGameTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import org.bson.Document;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Date;

public class GameEndListener implements Listener {

    @EventHandler
    public void onEnd(GameEndEvent event) {
        Game game = event.getGame();
        Team winner = event.getWinner();
        Team loser = event.getLoser();
        game.setWinner(winner);
        game.setState(GameState.ENDED);

        if (winner == null || loser == null) {
            game.getMap().placeFlag(game.getRed());
            game.getMap().placeFlag(game.getBlue());
            game.getRed().getPlayers().clear();
            game.getBlue().getPlayers().clear();
            game.getAudience().showTitle(Title.title(MM.deserialize("<yellow>Tied"), MM.deserialize("")));
            LogModule.GAME.log("Ending game. GameEndListener was triggered.");
            new ResetGameTask(game);
            return;
        }

        winner.getPlayers().forEach(player -> {
            player.getData().setWinStreak(player.getData().getWinStreak() + 1);
        });

        loser.getPlayers().forEach(player -> {
            player.getData().setWinStreak(0);
        });

        winner.getAudience().playSound(Sound.sound(Key.key("entity.firework_rocket.launch"), Sound.Source.MASTER, 1, 1));
        Color color = winner.getTeamColor() == game.getRed().getTeamColor() ? Color.RED : Color.BLUE;
        new CongratulationEffectTask(winner.getPlayers(), color,3);
        winner.getAudience().showTitle(Title.title(
                MM.deserialize(
                        "<#3ff21b>You won!"
                ),
                MM.deserialize("<gray>Congratulations!!")));

        loser.getAudience().playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, (float) 0.5));
        loser.getAudience().showTitle(Title.title(
                MM.deserialize(
                        "<gray>Team %s<gray> has won!".formatted(
                                winner.getTeamColor().getPrefix())
                ),
                MM.deserialize("<gray>Better luck next time!")));

        game.getPlayers().forEach(player -> {
            player.sendMessage(MM.deserialize("<yellow>You have earned <green>%s <yellow>experience during the match!".formatted(game.getXPCollector().read(player))));
        });
        game.getXPCollector().sendAll();

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(false);

        // Push final game data to database.

        Main.getMongoManager().findOneAndUpdate(Main.getMongoManager().getDatabase().getCollection("game"),
                new Document("_id", game.getUuid().toString()),
                Updates.combine(
                        Updates.set("winner", winner.getTeamColor().toString()),
                        Updates.set("date_end", new Date())
                ),
                options);



        game.getMap().placeFlag(loser);
        game.getMap().placeFlag(winner);
        winner.getPlayers().clear();
        loser.getPlayers().clear();
        LogModule.GAME.log("Ending game. GameEndListener was triggered.");
        new ResetGameTask(game);
    }
}
