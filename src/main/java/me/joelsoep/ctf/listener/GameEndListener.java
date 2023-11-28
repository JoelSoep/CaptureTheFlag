package me.joelsoep.ctf.listener;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Updates;
import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.event.GameEndEvent;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.game.task.CongratulationEffectTask;
import me.joelsoep.ctf.game.task.ResetGameTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GameEndListener implements Listener {

    private Plugin plugin;

    public GameEndListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        Game game = event.getGame();
        Team winner = event.getWinner();
        Team loser = event.getLoser();
        MapLoader mapLoader = new MapLoader(plugin);
        game.setWinner(winner);
        game.setState(GameState.ENDED);

        winner.getAudience().playSound(Sound.sound(Key.key("entity.firework_rocket.launch"), Sound.Source.MASTER, 1, 1));
        Color color = winner.getTeamColor() == game.getRed().getTeamColor() ? Color.RED : Color.BLUE;
        new CongratulationEffectTask(plugin, winner.getPlayers(), color,3);
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

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(false);

        // Push final game data to database.

        Main.mongo.findOneAndUpdateAsync(Main.mongo.getGameCollection(),
                new Document("_id", game.getUuid().toString()),
                Updates.combine(
                        Updates.set("winner", winner.getTeamColor().toString())
                ),
                options
        );

        Main.mongo.setGameCount(Main.mongo.getGameCount() + 1);



        mapLoader.reloadMap();
        mapLoader.placeFlag(loser);
        mapLoader.placeFlag(winner);
        winner.getPlayers().clear();
        loser.getPlayers().clear();
        new ResetGameTask(plugin, game);

     //  Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      //     @Override
     //      public void run() {
    //            Bukkit.spigot().restart();
    //        }
  //      }, 400L);
    }
}
