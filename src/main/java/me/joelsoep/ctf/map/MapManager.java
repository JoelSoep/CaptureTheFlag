package me.joelsoep.ctf.map;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.map.type.GameMap;
import me.joelsoep.ctf.map.type.LobbyMap;
import org.bukkit.Bukkit;
import org.reflections.Reflections;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapManager {

    private HashMap<String, Map> maps;
    private LobbyMap lobby;

    public MapManager() {
        loadLobby("me.joelsoep.ctf.map.type.lobby");
        loadGames("me.joelsoep.ctf.map.type.game");
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
                this.maps.put(map.getName(), gameMap);
                Bukkit.getLogger().info("Loaded gameMap %s".formatted(gameMap.getName()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }




}
