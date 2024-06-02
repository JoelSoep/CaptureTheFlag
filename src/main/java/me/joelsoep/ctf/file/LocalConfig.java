package me.joelsoep.ctf.file;

import lombok.Data;
import me.joelsoep.ctf.Main;

@Data
public class LocalConfig {
    public static boolean ALLOW_CUSTOM_TEAMS = false;

    public static boolean FORCE_GAME_MODE = false;

    public static String MONGO_CONNECTION_STRING = Main.getInstance().getConfig().getString("mongodb-uri");


}
