package me.joelsoep.ctf.file;

import lombok.Data;
import me.joelsoep.ctf.Main;

@Data
public class LocalConfig {
    public static boolean ALLOW_CUSTOM_TEAMS = Main.getInstance().getConfig().getBoolean("allow_custom_teams");

    public static boolean FORCE_GAME_MODE = Main.getInstance().getConfig().getBoolean("force_game_mode");

    public static String MONGO_CONNECTION_STRING = Main.getInstance().getConfig().getString("mongodb-uri");


}
