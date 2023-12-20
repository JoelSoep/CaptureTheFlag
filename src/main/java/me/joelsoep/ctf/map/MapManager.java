package me.joelsoep.ctf.map;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.map.type.LobbyMap;
import me.joelsoep.ctf.util.RandomUtil;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapManager {

    private static final HashMap<String, Map> gameMaps = new HashMap<>();
    private LobbyMap lobby;

    public MapManager() {
        loadLobby("me.joelsoep.ctf.map.type.lobby");
        loadGames("me.joelsoep.ctf.map.type.game");
        GameMap gameMap = getRandomGameMap();
        Main.game.createTeams(gameMap);
        Main.game.setMap(gameMap);
        Main.game.getMap().placeFlag(Main.game.getRed());
        Main.game.getMap().placeFlag(Main.game.getBlue());
    }

    public static String getWorldFolderPath() {
        String worldFolderPath = Main.getInstance().getDataFolder() + File.separator + "maps" + File.separator;
        new File(worldFolderPath).mkdirs();
        return worldFolderPath;
    }

    public static String getSchematicFolderPath() {
        String schematicFolderPath = Main.getInstance().getDataFolder() + File.separator + "schematics" + File.separator;
        new File (schematicFolderPath).mkdirs();
        return schematicFolderPath;
    }

    public static GameMap getRandomGameMap() {
        return (GameMap) gameMaps.values().stream().toList().get(RandomUtil.get().nextInt(gameMaps.size()));
    }

    public static List<Map> getGameMaps() {
        return gameMaps.values().stream().toList();
    }

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
                Bukkit.getLogger().info("Added content to map %s.".formatted(gameMap.getName()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
