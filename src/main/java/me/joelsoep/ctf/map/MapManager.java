package me.joelsoep.ctf.map;

import io.papermc.lib.PaperLib;
import lombok.Getter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.file.FileManager;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.board.LeaderboardService;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.map.type.LobbyMap;
import me.joelsoep.ctf.util.EmptyChunkGenerator;
import me.joelsoep.ctf.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MapManager {

    private static final HashMap<String, GameMap> gameMaps = new HashMap<>();
    @Getter
    private static LobbyMap lobby;

    public MapManager() {
        loadLobby("me.joelsoep.ctf.map.type.lobby");
        loadGames("me.joelsoep.ctf.map.type.game");
        if (gameMaps.values().size() == 0) {
            LogModule.MAP.severe("NO MAPS FOUND!!");
            Bukkit.shutdown();
            return;
        }
        GameMap gameMap = getRandomGameMap();
        Main.getGame().createTeams(gameMap);
        registerMapToGame(gameMap, Main.getGame());
    }

    public static GameMap getRandomGameMap() {
        return gameMaps.values().stream().toList().get(RandomUtil.get().nextInt(gameMaps.size()));
    }

    public static List<GameMap> getGameMaps() {
        return gameMaps.values().stream().toList();
    }

    public static GameMap getGameMap(String name) {
        return gameMaps.get(name);
    }

    public void loadLobby(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends LobbyMap>> mapClasses = reflections.getSubTypesOf(LobbyMap.class);

        if (mapClasses.stream().findFirst().isEmpty()) {
            LogModule.MAP.severe("Couldn't find any lobbies! Shutting down...");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            return;
        }
        Class<? extends LobbyMap> lobbyClass = mapClasses.stream().findFirst().get();

        try {
            LobbyMap lobby = lobbyClass.getDeclaredConstructor().newInstance();
            CompletableFuture<Boolean> future = lobby.isGloballyEnabled();
            LogModule.MAP.log("Initiating maploader for lobbymap %s".formatted(lobby.getName()));
            boolean enabled = future.join();
            LogModule.MAP.log("Joined future!");
            if (!enabled) {
                LogModule.MAP.log("Did not enable lobby map %s, as it is disabled.".formatted(lobby.getName()));
                return;
            }
            MapManager.lobby = lobby;
            LogModule.MAP.log("Loaded lobby %s".formatted(lobby.getName()));

            if (Bukkit.getWorld(lobby.getWorld().getName()) == null) {
                return;
            }

            if (lobby.getState() == MapState.READY) {
                return;
            }

            lobby.load();
            lobby.setState(MapState.READY);
            LeaderboardService leaderboardService = new LeaderboardService();
            leaderboardService.setLocationsAndCreate(lobby);
            LogModule.MAP.log("Added content to lobby %s.".formatted(lobby.getName()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void loadGames(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends GameMap>> mapClasses = reflections.getSubTypesOf(GameMap.class);

        if (mapClasses.stream().findFirst().isEmpty()) {
            LogModule.MAP.severe("Couldn't find any game maps! Shutting down...");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            return;
        }
        mapClasses.forEach(map -> {
            try {
                GameMap gameMap = map.getDeclaredConstructor().newInstance();
                CompletableFuture<Boolean> future = gameMap.isGloballyEnabled();
                LogModule.MAP.log("Initiating maploader for gamemap %s".formatted(gameMap.getName()));
                boolean enabled = future.join();
                LogModule.MAP.log("Joined future!");
                if (!enabled) {
                    LogModule.MAP.log("Did not enable game map %s, as it is disabled.".formatted(gameMap.getName()));
                    return;
                }
                gameMaps.put(gameMap.getName(), gameMap);
                LogModule.MAP.log("Loaded gameMap %s".formatted(gameMap.getName()));

                if (Bukkit.getWorld(gameMap.getWorld().getName()) == null) {
                    return;
                }

                gameMap.load();
                gameMap.setState(MapState.READY);
                LogModule.MAP.log("Added content to gameMap %s.".formatted(gameMap.getName()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void registerMapToGame(GameMap map, Game game) {
        Team red = game.getRed();
        Team blue = game.getBlue();

        blue.setSpawn(map.getBlueSpawnLocation());
        blue.getFlag().setLocation(map.getBlueFlagLocation());

        red.setSpawn(map.getRedSpawnLocation());
        red.getFlag().setLocation(map.getRedFlagLocation());

        map.placeFlag(Main.getGame().getBlue());
        map.placeFlag(Main.getGame().getRed());
        game.setMap(map);
    }

    public static @Nullable
    World createEmptyWorld(String name) {
        // this has a bug wth the letter d for some reason String sanitizedWorldName = (this.prefix + this.name).replaceAll("[^a-z0-9/._-]", "_");
        WorldCreator worldCreator = new WorldCreator(FileManager.getWorldFolderPath().replace('\\', '/') + name);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new EmptyChunkGenerator());
        World world = Bukkit.createWorld(worldCreator);
        if (world == null) {
            LogModule.MAP.warning("World %s is null, attempting to get access to it...".formatted(name));
            world = Bukkit.getWorld(name);
        }

        if (world == null) {
            LogModule.MAP.severe("FATAL: World %s couldn't be found. Aborting world creation...".formatted(name));
            return null;
        }

        world.setKeepSpawnInMemory(false);
        PaperLib.getChunkAtAsync(world.getSpawnLocation(), true);
        return world;
    }

    public static @Nullable World createEmptyWorld(String name, ChunkGenerator generator) {
        // this has a bug wth the letter d for some reason String sanitizedWorldName = (this.prefix + this.name).replaceAll("[^a-z0-9/._-]", "_");
        WorldCreator worldCreator = new WorldCreator(FileManager.getWorldFolderPath() + name);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(generator);
        World world = Bukkit.createWorld(worldCreator);
        if (world == null) {
            LogModule.MAP.warning("World %s is null, attempting to get access to it...".formatted(name));
            world = Bukkit.getWorld(name);
        }

        if (world == null) {
            LogModule.MAP.severe("FATAL: World %s couldn't be found. Aborting world creation...".formatted(name));
            return null;
        }

        world.setKeepSpawnInMemory(true);
        return world;
    }
}
