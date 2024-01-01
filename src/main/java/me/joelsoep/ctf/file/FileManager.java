package me.joelsoep.ctf.file;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.logger.LogModule;
import org.bukkit.Bukkit;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {
    public static final String DOMAIN = "https://dl.soep.me/";

    public static boolean requestFile(String fileName, String resource) {
            if (!new File(Main.getInstance().getDataFolder(), resource).exists()) {
                try {
                    if (!Main.getInstance().getDataFolder().exists()) {
                        Main.getInstance().getDataFolder().mkdirs();
                    }
                    LogModule.FILE.log("Downloading " + fileName + "...");
                    Files.copy(new URL(DOMAIN + fileName).openStream(), Paths.get(Main.getInstance().getDataFolder() + File.separator + resource));
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
}
