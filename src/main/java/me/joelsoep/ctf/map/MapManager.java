package me.joelsoep.ctf.map;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.game.board.LeaderboardService;
import me.joelsoep.ctf.game.team.Team;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.map.type.LobbyMap;
import me.joelsoep.ctf.util.RandomUtil;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapManager {

    private static final HashMap<String, Map> gameMaps = new HashMap<>();
    private static LobbyMap lobby;

    public MapManager() {
        loadLobby("me.joelsoep.ctf.map.type.lobby");
        loadGames("me.joelsoep.ctf.map.type.game");
        GameMap gameMap = getRandomGameMap();
        Main.game.createTeams(gameMap);
        Main.game.setMap(gameMap);
        this.registerMapToGame(gameMap, Main.game);
    }

    public static GameMap getRandomGameMap() {
        return (GameMap) gameMaps.values().stream().toList().get(RandomUtil.get().nextInt(gameMaps.size()));
    }

    public static List<Map> getGameMaps() {
        return gameMaps.values().stream().toList();
    }

    public static LobbyMap getLobby() { return lobby; }

    public void loadLobby(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends LobbyMap>> mapClasses = reflections.getSubTypesOf(LobbyMap.class);

        if (mapClasses.stream().findFirst().isEmpty()) {
            Bukkit.getLogger().severe("Couldn't find any lobbies! Shutting down...");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            return;
        }
        Class<? extends LobbyMap> lobbyClass = mapClasses.stream().findFirst().get();

        try {
            LobbyMap lobby = lobbyClass.getDeclaredConstructor().newInstance();
            this.lobby = lobby;
            Bukkit.getLogger().info("Loaded lobby %s".formatted(lobby.getName()));

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
            Bukkit.getLogger().info("Added content to lobby %s.".formatted(lobby.getName()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void loadGames(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends GameMap>> mapClasses = reflections.getSubTypesOf(GameMap.class);

        if (mapClasses.stream().findFirst().isEmpty()) {
            Bukkit.getLogger().severe("Couldn't find any game maps! Shutting down...");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            return;
        }
        mapClasses.forEach(map -> {
            try {
                GameMap gameMap = map.getDeclaredConstructor().newInstance();
                gameMaps.put(map.getName(), gameMap);
                Bukkit.getLogger().info("Loaded gameMap %s".formatted(gameMap.getName()));

                if (Bukkit.getWorld(gameMap.getWorld().getName()) == null) {
                    return;
                }

                gameMap.load();
                gameMap.setState(MapState.READY);
                Bukkit.getLogger().info("Added content to gameMap %s.".formatted(gameMap.getName()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void registerMapToGame(GameMap map, Game game) {
        Team red = game.getRed();
        Team blue = game.getBlue();

        blue.setSpawn(map.getBlueSpawnLocation());
        blue.getFlag().setLocation(map.getBlueFlagLocation());

        red.setSpawn(map.getRedSpawnLocation());
        red.getFlag().setLocation(map.getRedFlagLocation());
    }
}
