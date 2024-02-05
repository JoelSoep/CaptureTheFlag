package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.economy.EconomyManager;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.game.task.ResetGameTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.player.experience.Level;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.title.Title;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.TestOnly;

import java.util.concurrent.atomic.AtomicReference;

@TestOnly
@CommandAlias("debug|dbg")
@CommandPermission("ctf.admin")
public class DebugCommand extends BaseCommand {
    Game game = Main.getGame();

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
            CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
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
            CTFPlayer ctfPlayer = PlayerService.getPlayers().get(playerName);
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

        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
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
        game.setState(GameState.ENDED);
        player.sendMessage(MM.deserialize("<yellow>Ending game..."));

        game.getMap().placeFlag(game.getBlue());
        game.getMap().placeFlag(game.getRed());
        game.getBlue().getPlayers().clear();
        game.getRed().getPlayers().clear();

        game.getAudience().showTitle(Title.title(MM.deserialize("<yellow>Tied"), MM.deserialize("<gray>Game was ended by an operator")));
        new ResetGameTask(game);
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

   // @Subcommand("setmaxplayers")
    //public void setMaxPlayers(Player player, int integer) {
   //     Game game = Main.getGame();
   //     game.getMap().setMaxPlayers(integer);
   //     player.sendMessage(MM.deserialize("<gray>Updated max players to <yellow>" + game.getMap().getMaxPlayers()));
   // }

    @Subcommand("worldtp")
    @CommandCompletion("@worlds")
    public void worldTP(Player player, World world) {
        player.teleport(world.getSpawnLocation());
    }

    @Subcommand("allowteamselector")
    public void teamSelector(Player player, boolean value) {
        LocalConfig.ALLOW_CUSTOM_TEAMS = value;
        player.sendMessage(MM.deserialize("<gray>Updated value to <yellow>%s<gray>.".formatted(value)));
    }

    @Subcommand("givespecial")
    @CommandCompletion("@players")
    public void giveSpecial(Player player, OnlinePlayer target, @Single String specialString, @Optional Integer amount) {
        if (target == null) {
            player.sendMessage(MM.deserialize("Target is null!"));
            return;
        }

        BaseSpecial<?> special = Main.getSpecialService().getSpecial(specialString);
        if (special == null) {
            player.sendMessage(MM.deserialize("special is null!"));
            return;
        }
        ItemStack itemStack = special.getItem();
        if (itemStack == null) {
            player.sendMessage(MM.deserialize("itemstack is null!"));
            return;
        }

        itemStack.setAmount(amount != null ? amount : 1);
        target.getPlayer().getInventory().addItem(itemStack);
    }

    @Subcommand("setmap")
    public void setMap(Player player, String mapName) {
        GameMap map = MapManager.getGameMap(mapName);
        if (map == null) {
            player.sendMessage(MM.deserialize("The map is null."));
            return;
        }
        Game game = Main.getGame();
        MapManager.registerMapToGame(map, Main.getGame());
        game.setMap(map);
        player.sendMessage("Updated map.");
    }

    @Subcommand("listmaps")
    public void listMaps(Player player) {
        AtomicReference<String> str = new AtomicReference<>("Maps: <br>");
        MapManager.getGameMaps().forEach(map -> {
            String addition = map.getName() + "<br>";
            str.set(str.get().concat(addition));
        });
        player.sendMessage(MM.deserialize(str.get()));
    }

    @Subcommand("xp")
    public void xp(Player player, int amount) {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }

        Level.gainExperience(ctfPlayer, amount);
    }

    @Subcommand("setbalance")
    public void setBalance(Player player, @Optional OnlinePlayer target, int amount) {
        CTFPlayer ctfPlayer = PlayerService.getPlayers().get(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }

        CTFPlayer ctfTarget = PlayerService.getPlayers().get(target.getPlayer().getName());
        if (ctfTarget == null) {
            ctfTarget = ctfPlayer;
            return;
        }

        player.sendMessage(MM.deserialize("Transaction succession state: " + EconomyManager.set(ctfTarget, amount)));
    }

}
