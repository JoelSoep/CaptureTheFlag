package me.joelsoep.ctf.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.eduardomcb.discord.webhook.WebhookClient;
import com.mongodb.client.result.DeleteResult;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.CosmeticsManager;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.database.MongoManager;
import me.joelsoep.ctf.database.subscriber.IDeleteOneSubscriber;
import me.joelsoep.ctf.discord.DiscordWebhook;
import me.joelsoep.ctf.economy.EconomyManager;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.file.LocalConfig;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.board.ActionbarPriority;
import me.joelsoep.ctf.game.gui.CosmeticStoreGUI;
import me.joelsoep.ctf.game.gui.GameSettingsGui;
import me.joelsoep.ctf.game.gui.TestGui;
import me.joelsoep.ctf.game.kit.BaseKit;
import me.joelsoep.ctf.game.setting.GameSetting;
import me.joelsoep.ctf.game.special.BaseSpecial;
import me.joelsoep.ctf.game.task.ResetGameTask;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.MapManager;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.player.experience.Level;
import me.joelsoep.ctf.server.BaseServer;
import me.joelsoep.ctf.server.CTFServer;
import me.joelsoep.ctf.server.packet.packets.GoodbyePacket;
import me.joelsoep.ctf.server.socket.SocketSender;
import me.joelsoep.ctf.server.packet.packets.RetrievePlayerPacket;
import me.joelsoep.ctf.store.StoreCosmeticItem;
import me.joelsoep.ctf.store.StoreItem;
import me.joelsoep.ctf.store.StoreManager;
import me.joelsoep.ctf.store.items.cosmetic.deathMessage.TookLItem;
import me.joelsoep.ctf.util.Hotbar;
import me.joelsoep.ctf.util.MM;
import me.joelsoep.ctf.util.test.DisplayEntities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@TestOnly
@CommandAlias("debug|dbg")
@CommandPermission("ctf.debug")
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
                <gray>- etc, im too lazy to add all the cmds,
                <gray>- because i am too lazy
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

        ItemStack itemCloned = itemStack.clone();

        itemCloned.setAmount(amount != null ? amount : 1);
        target.getPlayer().getInventory().addItem(itemCloned);
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

        Cosmetic cosmetic = Main.getCosmeticsManager().getCosmetic(cosmeticType, cosmeticName);
        if (cosmetic == null) {
            player.sendMessage(MM.deserialize("The cosmetic is null!"));
            return;
        }
        ctfPlayer.getCosmetics().setAppliedCosmetic(cosmeticType, cosmetic);
        ctfPlayer.sendMessage(MM.deserialize("Set %s as your cosmetic for type %s".formatted(cosmetic.getName(), cosmeticType.toString())));
    }

    @Subcommand("listcosmetics")
    public void listCosmetics(Player player, CosmeticType type) {
        HashMap<String, Cosmetic> map = CosmeticsManager.getCosmetics().get(type);
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

    @Subcommand("dpe-scale")
    public void DPEScale(Player player, Location location, Material material, int scale) {
        if (location.equals("~")) {
            location = player.getLocation();
        }
        DisplayEntities DPE = new DisplayEntities(location);
        DPE.scale(material, scale);
        player.sendMessage(MM.deserialize("<gray>Scaled!"));
    }

    @Subcommand("glow")
    public void glow(Player player, ChatColor color) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }

        Bukkit.getOnlinePlayers().forEach(p -> {
            try {
                Main.getGlowingEntitiesAPI().setGlowing(player, p, color);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Subcommand("unglow")
    public void unglow(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player.getName());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>You are not registered as a CTFPlayer!"));
            return;
        }

        Bukkit.getOnlinePlayers().forEach(p -> {
            try {
                Main.getGlowingEntitiesAPI().unsetGlowing(player, p);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Subcommand("sendRetrievalPacket")
    public void sendRetrievalPacket(Player player, String uuid) {
        RetrievePlayerPacket packet =  new RetrievePlayerPacket.Builder()
                .withUUID(uuid)
                .withTeam("red")
                .withKey(Main.getGameServer().getApiConfig().key())
                .withSender(Main.getGameServer().getId())
                .build();
        SocketSender.send(Main.getGameServer().getIp(), Main.getGameServer().getPort(), packet);
    }

    @Subcommand("msg")
    public void simulateMessage(Player player, String message) {
        player.sendMessage(MM.deserialize(message));
    }

    @Subcommand("rotatingblock")
    public void rotatingBlock(Player player, int size, int radians) {
        Location location = player.getLocation();
        DisplayEntities dpe = new DisplayEntities(location);
        dpe.rotatingLuckyBlock(size, radians);
    }

    @Subcommand("unregisterServer")
    public void unregisterServer(Player player, @Single String serverName) {
        if (!Main.getGameServer().getOnlineServers().containsKey(serverName)) {
            player.sendMessage("<red>Couldn't find server in server registry.");
            return;
        }
        BaseServer bs = Main.getGameServer().getOnlineServers().get(serverName);
        GoodbyePacket packet = new GoodbyePacket.Builder()
                .withDefaultValues()
                .withSender(serverName)
                .withHost(bs.ip())
                .withPort(bs.port())
                .withMode(bs.mode())
                .build();
        SocketSender.sendAll(packet, true);
        MongoManager.deleteOne(MongoManager.getCollection("server"), new Document("_id", serverName), new IDeleteOneSubscriber() {
            @Override
            public void onResult(DeleteResult result) {
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        player.sendMessage(MM.deserialize("<gray>Removed server <yellow>%s <gray>from all registries.".formatted(serverName)));
    }

    @Subcommand("refreshAdditionalMessage")
    public void refresh(Player sender) {
        PlayerService.getPlayers().values().forEach(player -> {
            YamlConfiguration config = FileManager.getDefaultConfig();
            String message = config.getString("additional-message") == null ? "<gray>" : config.getString("additional-message");
            player.getScoreboardHandler().setAdditionalMessage(message);
        });
        sender.sendMessage(MM.deserialize("<gray>Refreshed and updated all scoreboards."));
    }

    @Subcommand("reloadhotbar")
    public void reloadHotbar(Player player, @Optional OnlinePlayer onlinePlayer) {
        if (onlinePlayer == null) {
            new Hotbar(player);
            player.sendMessage(MM.deserialize("<gray>Done!"));
            return;
        }

        new Hotbar(onlinePlayer.getPlayer());
        player.sendMessage(MM.deserialize("<gray>Reloaded hotbar for player %s".formatted(onlinePlayer.getPlayer().getName())));
    }

    @Subcommand("reloadhotbars")
    public void reloadHotbars(Player p) {
        PlayerService.getPlayers().forEach((name, player) -> {
            new Hotbar(player.getPlayer());
        });
        p.sendMessage(MM.deserialize("<gray>Done!"));
    }

    @Subcommand("addwaitingsystemmessage")
    public void addWaitingSystemMessage(Player player, OnlinePlayer target, String message) {
        if (target == null) {
            player.sendMessage(MM.deserialize("<red>This player does not exist."));
            return;
        }

        CTFPlayer ctfPlayer = PlayerService.getPlayer(target.getPlayer());
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>Could not find the target's profile."));
            return;
        }

        ctfPlayer.addWaitingSystemMessage(message);
        player.sendMessage(MM.deserialize("<gray>Added message to %s's waiting system messages.".formatted(target.getPlayer().getName())));
    }

    @Subcommand("earlystorepurchasetest")
    public void earlyStorePurchaseTest(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
        if (ctfPlayer == null) {
            return;
        }
        StoreManager.purchase(new TookLItem(), ctfPlayer);
    }

    @Subcommand("store")
    public void openStore(Player player) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
        if (ctfPlayer == null) {
            return;
        }
        new CosmeticStoreGUI(ctfPlayer);
    }

    @Subcommand("reloadstore")
    public void reloadStore(Player player) {
        player.sendMessage(MM.deserialize("<gray>Reloading store..."));
        StoreManager.reload();
        player.sendMessage(MM.deserialize("<gray>Done!"));
    }

    @Subcommand("verifyApiKey")
    public void verifyApiKey(Player player) {
        if (!CTFServer.isEnabled()) {
            player.sendMessage(MM.deserialize("There is no API key as the CTF-server is disabled."));
            return;
        }

        player.sendMessage(MM.deserialize(Main.getGameServer().getApiConfig().key()));
    }

    @Subcommand("createBaseCosmetic")
    public void createBaseCosmetic(Player player, CosmeticType type, @Single String name, int price) {
        player.sendMessage("creating");
        StoreCosmeticItem item = new StoreCosmeticItem(name, Main.getCosmeticsManager().getCosmetic(type, name.split("\\.")[1]), price, 1);
        StoreManager.getCloud().pushItem(item);
        player.sendMessage("done");
    }

    @Subcommand("opentestgui")
    public void openTestGui(Player player) {
        new TestGui(player);
    }

    @Subcommand("appliedCosmetic")
    public void appliedCosmetic(Player player, CosmeticType type, @Optional OnlinePlayer onlinePlayer) {
        if (onlinePlayer == null) {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
            if (ctfPlayer == null) {
                player.sendMessage(MM.deserialize("<red>An error occurred while trying to retrieve your profile."));
                return;
            }

            Cosmetic applied = ctfPlayer.getCosmetics().getAppliedCosmetic(type);
            if (applied == null) {
                player.sendMessage(MM.deserialize("You do not have a cosmetic applied for type %s".formatted(type.toString())));
            } else {
                player.sendMessage(MM.deserialize("You have cosmetic %s applied for type %s".formatted(applied.getName(), type.toString())));
            }
        } else {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(onlinePlayer.getPlayer());
            if (ctfPlayer == null) {
                player.sendMessage("<red>An error occurred while trying to retrieve your profile.");
                return;
            }

            Cosmetic applied = ctfPlayer.getCosmetics().getAppliedCosmetic(type);
            if (applied == null) {
                player.sendMessage(MM.deserialize("%s does not have a cosmetic applied for type %s".formatted(onlinePlayer.getPlayer().getName(), type.toString())));
            } else {
                player.sendMessage(MM.deserialize("%s has cosmetic %s applied for type %s".formatted(onlinePlayer.getPlayer().getName(), applied.getName(), type.toString())));
            }
        }
    }

    @Subcommand("setinfinitegametime")
    public void setInfiniteGameTime(Player player, boolean value) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>An error occurred while trying to retrieve your profile."));
            return;
        }

        Game game = ctfPlayer.getGame();
        if (game == null) {
            player.sendMessage(MM.deserialize("<red>You are not in a game right now."));
            return;
        }

        game.setInfiniteGameTime(value);
        player.sendMessage(MM.deserialize("<gray>Done!"));
    }

    @Subcommand("locationblock")
    public void locationBlock(Player player) {
        Block block = player.getLocation().getBlock();
        player.sendMessage(MM.deserialize("Type: " + block.getType()));
    }

    @Subcommand("gamesettingsgui")
    public void gameSettingsGui(Player player, @Optional OnlinePlayer onlinePlayer) {
        CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
        if (ctfPlayer == null) {
            player.sendMessage(MM.deserialize("<red>An error occurred while trying to retrieve your profile."));
            return;
        }
        if (onlinePlayer == null) {
            new GameSettingsGui(ctfPlayer);
            return;
        } else {
            CTFPlayer onlineCtfPlayer = PlayerService.getPlayer(onlinePlayer.getPlayer());
            if (onlineCtfPlayer == null) {
                player.sendMessage(MM.deserialize("<red>Couldn't find profile for player %s".formatted(onlinePlayer.getPlayer().getName())));
                return;
            }
            new GameSettingsGui(onlineCtfPlayer);
            player.sendMessage(MM.deserialize("<gray>Done!"));
        }
    }

    @Subcommand("givepaperhp")
    public void givepaperhp(Player player) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
        meta.setColor(Color.RED);
        meta.displayName(MM.deserialize("<red>%s Hearts".formatted(10)));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        List<Component> lore = new ArrayList<>();
        lore.add(MM.deserialize("<gray>Left-click to change"));


        meta.lore(lore);
        itemStack.setItemMeta(meta);
        itemStack.setData(
                DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                        .hiddenComponents(Set.of(DataComponentTypes.POTION_CONTENTS, DataComponentTypes.ENCHANTMENTS))
                        .build()
        );
        player.getInventory().addItem(itemStack);
    }

    @Subcommand("uuid")
    public void uuid(Player player, @Optional OnlinePlayer onlinePlayer) {
        if (onlinePlayer == null) {
            player.sendMessage(MM.deserialize("Your uuid is <click:copy_to_clipboard:%s>%s. Click here to copy.</click>".formatted(player.getUniqueId(), player.getUniqueId())));
            return;
        } else {
            player.sendMessage(MM.deserialize("%s's uuid is <click:copy_to_clipboard:%s>%s. Click here to copy.</click>".formatted(player.getName(), player.getUniqueId(), player.getUniqueId())));
        }
    }

    @Subcommand("giveItemByMaterial")
    public void giveItemByMaterial(Player player, Material material) {
        ItemStack item = new ItemStack(material);
        player.getInventory().addItem(item);
        player.sendMessage("Done");
    }

    @Subcommand("checkplus")
    public void checkPlus(Player player, @Optional OnlinePlayer onlinePlayer) {
        if (onlinePlayer == null) {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
            if (ctfPlayer == null) {
                player.sendMessage("ctfplayer is null");
                return;
            }
            ctfPlayer.setPlus(player.hasPermission("ctf.plus"));
            player.sendMessage("done");
            return;
        } else {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(onlinePlayer.getPlayer());
            if (ctfPlayer == null) {
                player.sendMessage("ctfplayer is null");
                return;
            }
            ctfPlayer.setPlus(player.hasPermission("ctf.plus"));
            player.sendMessage("done");
            return;
        }
    }

    @Subcommand("setplus")
    public void setPlus(Player player, @Optional OnlinePlayer onlinePlayer, boolean value) {
        if (onlinePlayer == null) {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(player);
            if (ctfPlayer == null) {
                player.sendMessage("ctfplayer is null");
                return;
            }
            ctfPlayer.setPlus(value);
            player.sendMessage("done");
            return;
        } else {
            CTFPlayer ctfPlayer = PlayerService.getPlayer(onlinePlayer.getPlayer());
            if (ctfPlayer == null) {
                player.sendMessage("ctfplayer is null");
                return;
            }
            ctfPlayer.setPlus(value);
            player.sendMessage("done");
            return;
        }
    }

    @Subcommand("allowbypassdatasuspicioncheck")
    public void allowBypassDataSuspicionCheck(Player player, boolean value) {
        if (!player.getUniqueId().equals(UUID.fromString("b35f3a61-8e25-4274-87ce-93a0e604df2c"))) {
            player.sendMessage(MM.deserialize("<red>This is a very dangerous command!"));
            player.sendMessage(MM.deserialize("<red>Do not touch debug commands if you don't know what they do!"));
            player.sendMessage(MM.deserialize("<red>This attempt will be logged."));
            player.sendMessage(MM.deserialize("<red>Execution of this command has been denied."));
            DiscordWebhook.sendMessage(new WebhookClient.Callback() {
                @Override
                public void onSuccess(String response) {
                    LogModule.CORE.log("logged unauthorized allowbypasssuspicioncheck usage by player %s".formatted(player.getUniqueId().toString()));
                }

                @Override
                public void onFailure(int statusCode, String errorMessage) {
                    LogModule.CORE.log(statusCode + " err: " + errorMessage);
                }
            }, "user %s attempted to use command /debug allowbypassdatasuspicioncheck!".formatted(player.getName() + "(" + player.getUniqueId().toString() + ")"));
        }

        Main.getSettings().setAllowBypassDataSuspicionCheck(value);
        player.sendMessage("done");
    }
}
