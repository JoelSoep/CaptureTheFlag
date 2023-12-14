package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.joelsoep.ctf.CTFPlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.game.map.MapLoader;
import me.joelsoep.ctf.game.task.ResetGameTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandAlias("debug|dbg")
@CommandPermission("ctf.admin")
public class DebugCommand extends BaseCommand {
    private final Plugin plugin;

    Game game = Main.game;

    public DebugCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @CatchUnknown
    @Subcommand("list")
    @Description("Debug command list.")
    public void onList(Player player, String[] args) {
        player.sendMessage(MM.deserialize("""
                <red>Debug command list:
                <gray>- list
                <gray>- team <player>
                <gray>- endgame
                <gray>- game
                <gray>- giveblocks
                """));
    }

    @Subcommand("team")
    @CommandCompletion("@players")
    @Description("Get information about a players team.")
    public void onTeam(Player player, String[] args) {
        if (args.length == 0) {
            CTFPlayer ctfPlayer = Main.playerList.get(player.getName());
            if (ctfPlayer == null) {
                player.sendMessage(MM.deserialize("Couldn't find CTFPlayer!"));
                return;
            }

            if (ctfPlayer.getTeam() == null) {
                player.sendMessage(MM.deserialize("This player is not in a team right now!"));
                return;
            }
            String gameTeam = game.getBlue().getPlayers().stream().map(map -> map.getPlayer().getName()).toList().contains(ctfPlayer.getPlayer().getName()) ? "BLUE" : game.getRed().getPlayers().stream().map(map -> map.getPlayer().getName()).toList().contains(ctfPlayer.getPlayer().getName()) ? "RED" : "No team";
            player.sendMessage(MM.deserialize("CTFPlayer Team for " + player.getName() + ": " + ctfPlayer.getTeam().getTeamColor().toString()));
            player.sendMessage(MM.deserialize("Game Team for " + ctfPlayer.getPlayer().getName() + ": " + gameTeam));
        } else {
            String playerName = args[0];
            CTFPlayer ctfPlayer = Main.playerList.get(playerName);
            if (ctfPlayer == null) {
                player.sendMessage(MM.deserialize("Couldn't find CTFPlayer!"));
                return;
            }

            if (ctfPlayer.getTeam() == null) {
                player.sendMessage(MM.deserialize("This player is not in a team right now!"));
                return;
            }

            String gameTeam = game.getBlue().getPlayers().stream().map(map -> map.getPlayer().getName()).toList().contains(ctfPlayer.getPlayer().getName()) ? "BLUE" : game.getRed().getPlayers().stream().map(map -> map.getPlayer().getName()).toList().contains(ctfPlayer.getPlayer().getName()) ? "RED" : "No team";
            player.sendMessage(MM.deserialize("<gray>CTFPlayer Team for " + ctfPlayer.getPlayer().getName() + ": <yellow>" + ctfPlayer.getTeam().getTeamColor().toString()));
            player.sendMessage(MM.deserialize("<gray>Game Team for " + ctfPlayer.getPlayer().getName() + ": <yellow>" + gameTeam));
        }
    }

    @Subcommand("game")
    @Description("Get information about your current game.")
    public void onGame(Player player) {
        player.sendMessage(MM.deserialize("<gray>The current game ID is <yellow>" + game.getUuid().toString()));
        player.sendMessage(MM.deserialize("<gray>The current state of this game is <yellow>" + game.getState().toString()));

        CTFPlayer ctfPlayer = Main.playerList.get(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }

        if (ctfPlayer.getGame() == null) {
            player.sendMessage(MM.deserialize("<red>You are not in a game!"));
            return;
        }
        player.sendMessage(MM.deserialize("<gray>Your game ID is <yellow>" + ctfPlayer.getGame().getUuid().toString()));
        player.sendMessage(MM.deserialize("<gray>The current state of your game is <yellow>" + ctfPlayer.getGame().getState().toString()));
        player.sendMessage(MM.deserialize("<gray>You are " + (ctfPlayer.getGame().getUuid().toString().equals(game.getUuid().toString()) ? "<green>synced." : "<red>NOT synced.")));
    }

    @Subcommand("endgame")
    @Description("Ends the current game.")
    public void onEnd(Player player) {
        if (game.getState() != GameState.RUNNING) {
            player.sendMessage(MM.deserialize("<red>The game has not started yet!"));
            return;
        }
        MapLoader mapLoader = new MapLoader(plugin);
        game.setState(GameState.ENDED);
        player.sendMessage(MM.deserialize("<yellow>Ending game..."));

        mapLoader.reloadMap();
        mapLoader.placeFlag(game.getBlue());
        mapLoader.placeFlag(game.getRed());
        game.getBlue().getPlayers().clear();
        game.getRed().getPlayers().clear();

        game.getAudience().showTitle(Title.title(MM.deserialize("<yellow>Tied"), MM.deserialize("<gray>Game was ended by an operator")));
        new ResetGameTask(plugin, game);
    }

    @Subcommand("giveblocks")
    @CommandCompletion("@players")
    @Description("Give a player blocks.")
    public void onGive(Player player, OnlinePlayer onlinePlayer, String team) {
        if (!(team.equals("red") || team.equals("blue"))) {
            player.sendMessage(MM.deserialize("<red>Please select a valid team! (red/blue)"));
            return;
        }
        Team finalTeam = null;

        switch (team) {
            case "red" -> finalTeam = game.getRed();
            case "blue" -> finalTeam = game.getBlue();
            default -> player.sendMessage(MM.deserialize("<red>Please select a valid team! (red/blue)"));
        }

        if (finalTeam == null) {
            player.sendMessage(MM.deserialize("The given team is null! Is the game running?"));
            return;
        }

        onlinePlayer.getPlayer().getInventory().addItem(BaseKit.getBlocks(finalTeam, 64));
    }

    @Subcommand("setmaxplayers")
    public void setMaxPlayers(Player player, int integer) {
        Game game = Main.game;
        game.setMaxPlayers(integer);
        player.sendMessage(MM.deserialize("<gray>Updated max players to <yellow>" + game.getMaxPlayers()));
    }
}
