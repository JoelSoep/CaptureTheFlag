package me.joelsoep.ctf.logger;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public enum LogModule {
    DATABASE("[DATABASE] "),
    COMMAND("[COMMANDMANAGER] "),
    UTILITY("[UTILITY] "),
    SPECIAL("[SPECIALSERVICE] "),
    KIT("[KITSERVICE] "),
    ADVANCEMENT("[ADVANCEMENTS] "),
    MAP("[MAPMANAGER] "),
    FILE("[FILEMANAGER] "),
    GAME("[GAME] "),
    CORE("[CORE] "),
    PLAYER("[PLAYERSERVICE] "),
    COSMETIC("[COSMETICS] ");

    private final String prefix;

    LogModule(String prefix) {
        this.prefix = prefix;
    }

    public void logOnly(Level level, String message) {
        CTFLogger.getLogger().log(level, prefix + message);
    }

    public void log(Level level, String message) {
        CTFLogger.getLogger().log(level, prefix + message);
        Bukkit.getLogger().log(level, prefix + message);
    }

    public void log(String message) {
        CTFLogger.getLogger().info(prefix + message);
        Bukkit.getLogger().info(prefix + message);
    }

    public void warning(String message) {
        CTFLogger.getLogger().warning(prefix + message);
        Bukkit.getLogger().warning(prefix + message);
    }

    public void severe(String message) {
        CTFLogger.getLogger().severe(prefix + message);
        Bukkit.getLogger().severe(prefix + message);
    }

    public String getPrefix() {
        return prefix;
    }
}
