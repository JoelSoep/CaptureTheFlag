package me.joelsoep.ctf.file;

import lombok.Data;
import me.joelsoep.ctf.Main;
import org.bukkit.configuration.file.FileConfiguration;

@Data
public class LocalConfig {

    private static FileConfiguration CONFIG = Main.getInstance().getConfig();

    public static boolean ALLOW_CUSTOM_TEAMS = CONFIG.getBoolean("allow_custom_teams");

    public static boolean FORCE_GAME_MODE = CONFIG.getBoolean("force_game_mode");

    public static boolean OVERRIDE_MCMANHUNT_CHAT_HANDLER = CONFIG.getBoolean("override-mcmanhunt-chat-handler");

    public static String MONGO_CONNECTION_STRING = CONFIG.getString("mongodb-uri");

    public static String MODE = CONFIG.getString("mode");

    public static boolean PROXY_MODE = CONFIG.getBoolean("proxy");

    public static String PROXY_MODE_ID = FileManager.getServerConfig().getString("id");

    public static String PROXY_MODE_HOST = FileManager.getServerConfig().getString("ip");

    public static int PROXY_MODE_PORT = FileManager.getServerConfig().getInt("port");

    public static String DISCORD_WEBHOOK_URL = CONFIG.getString("webhook-url");

    public static String DISCORD_WEBHOOK_AVATAR_URL = CONFIG.getString("webhook-avatar-url");

}
