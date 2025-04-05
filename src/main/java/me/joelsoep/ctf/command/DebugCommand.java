package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.economy.EconomyManager;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.board.ActionbarHandler;
import me.joelsoep.ctf.game.board.ActionbarPriority;
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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@TestOnly
@CommandAlias("debug|dbg")
@CommandPermission("ctf.debug|ctf.admin")
public final class DebugCommand extends BaseCommand {
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

    @Subcommand("setmaxplayers")
    public void setMaxPlayers(Player player, int integer) {
        Game game = Main.getGame();
        game.getMap().setMaxPlayers(integer);
        player.sendMessage(MM.deserialize("<gray>Updated max players to <yellow>" + game.getMap().getMaxPlayers()));
    }

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
        MapManager.registerMapToGame(map, Main.getGame());
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
        }

        player.sendMessage(MM.deserialize("Transaction succession state: " + EconomyManager.set(ctfTarget, amount)));
    }

    @Subcommand("simulatexp")
    public void simulate(Player player) {
        for (int i = 0; i < 150; i++) {
            Bukkit.broadcast(MM.deserialize("Level %s: %s".formatted(i + 1, Level.getXPForNextLevel(i))));
        }
    }

    @Subcommand("setcosmetic")
    public void setCosmetic(Player player, CosmeticType cosmeticType, String cosmeticName) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }

        Cosmetic cosmetic = Main.getCosmeticManager().getCosmetic(cosmeticType, cosmeticName);
        if (cosmetic == null) {
            player.sendMessage(MM.deserialize("The cosmetic is null!"));
            return;
        }
        ctfPlayer.getCosmetics().setAppliedCosmetic(cosmeticType, cosmetic);
        ctfPlayer.sendMessage(MM.deserialize("Set %s as your cosmetic for type %s".formatted(cosmetic.getName(), cosmeticType.toString())));
    }

    @Subcommand("listcosmetics")
    public void listCosmetics(Player player, CosmeticType type) {
        HashMap<String, Cosmetic> map = Main.getCosmeticManager().getCosmetics().get(type);
        if (map == null) {
            player.sendMessage(MM.deserialize("No cosmetics found."));
            return;
        }
        map.forEach((string, cosmetic) -> {
            player.sendMessage(MM.deserialize(string));
        });
    }

    @Subcommand("createworld")
    public void createWorld(Player player, @Single String name) {
        MapManager.createEmptyWorld(name);
        player.sendMessage(MM.deserialize("Created world with name %s".formatted(name)));
    }

    @Subcommand("loadworld")
    public void loadWorld(Player player, @Single String name) {
        String path = FileManager.getWorldFolderPath() + name;
        WorldCreator creator = new WorldCreator(path);
        Bukkit.createWorld(creator);
        player.sendMessage("done");
    }

    @Subcommand("listspecials")
    public void listSpecials(Player player) {
        List<BaseSpecial> specials = Main.getSpecialService().getAllSpecials();
        player.sendMessage(MM.deserialize("<gray>There are <yellow>%s <gray>active specials: \n".formatted(specials.size())));
        specials.forEach(special -> {
            player.sendMessage(MM.deserialize("<yellow>" + special.getName()));
        });
    }

    @Subcommand("sleep")
    public void sleep(Player player) {
        player.sleep(player.getLocation(), true);
    }

    @Subcommand("actionbarset")
    public void actionbarset(Player player, ActionbarPriority priority, @Optional String message) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }
        if (message == null) {
            message = "";
        }
        ctfPlayer.getActionbarHandler().setMessage(priority, message);
        player.sendMessage(MM.deserialize("<gray>Set message!"));
    }

    @Subcommand("actionbarsetwithtime")
    public void actionbarsetwithtime(Player player, ActionbarPriority priority, Long time, @Optional String message) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }

        if (message == null) {
            message = "";
        }
        ctfPlayer.getActionbarHandler().setMessage(priority, message, time);
        player.sendMessage(MM.deserialize("<gray>Set message!"));
    }

    @Subcommand("resetmapfeatures")
    public void resetmapfeatures(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }
        Main.getGame().getMap().resetFeatures();
        player.sendMessage(MM.deserialize("<gray>Reset map features!"));
    }
}
