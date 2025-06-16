package me.joelsoep.ctf.file;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {
    public static final String DOMAIN = "https://dl.soep.me/";
    public static final String SPECIAL_PATH = "me.joelsoep.ctf.game.special.specials";
    public static final String KIT_PATH = "me.joelsoep.ctf.game.kit.kits";

    public static boolean requestFile(String fileName, FileType type, String resource) {
            if (!new File(Main.getInstance().getDataFolder(), resource).exists()) {
                try {
                    if (!Main.getInstance().getDataFolder().exists()) {
                        Main.getInstance().getDataFolder().mkdirs();
                    }
                    LogModule.FILE.log("Downloading " + fileName + "...");
                    Files.copy(new URL(DOMAIN + type.getPath() + fileName).openStream(), Paths.get(Main.getInstance().getDataFolder().getPath(), resource));
                    return true;
                } catch (Exception e) {
                    LogModule.FILE.warning("Couldn't create " + resource);
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
    }

    public static String getWorldFolderPath() {
        String worldFolderPath = Main.getInstance().getDataFolder() + File.separator + "maps" + File.separator;
        new File(worldFolderPath).mkdirs();
        return worldFolderPath;
    }

    public static String getSchematicFolderPath() {
        String schematicFolderPath = Main.getInstance().getDataFolder() + File.separator + "schematics" + File.separator;
        new File(schematicFolderPath).mkdirs();
        return schematicFolderPath;
    }

    public static String getLogFolderPath() {
        String logFolderPath = Main.getInstance().getDataFolder() + File.separator + "logs" + File.separator;
        new File(logFolderPath).mkdirs();
        return logFolderPath;
    }

    public static String getServerPath() {
        String serverPath = Main.getInstance().getDataFolder() + File.separator + "server" + File.separator;
        new File(serverPath).mkdirs();
        return serverPath;
    }

    public static YamlConfiguration getServerConfig() {
        String path = getServerPath() + "server.yml";
        File file = new File(path);
        String internalPath = "server" + File.separator + "server.yml";
        if (!file.exists()) {
            Main.getInstance().saveResource(internalPath, true);
            LogModule.FILE.log("Created new server.yml.");
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public static void saveServerConfig() {
        String path = getServerPath() + File.separator + "server.yml";
        if (!(new File(path).exists())) {
            Main.getInstance().saveResource(path, true);
        }
    }
}
