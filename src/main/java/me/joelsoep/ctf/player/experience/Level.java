package me.joelsoep.ctf.player.experience;

import me.joelsoep.ctf.game.event.PlayerLevelUpEvent;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.util.MM;
import org.bukkit.Bukkit;

public class Level {
    // level 1
    private static final int BASE_XP = 100;

    public static int getLevel(CTFPlayer player) {
        return experienceToLevel(player);
    }

    private static int getXPForNextLevel(int level) {
        return (BASE_XP * (level+1) * (level+1)) - BASE_XP;
    }

    public static void gainExperience(CTFPlayer player, int xp) {
        int experience = player.getData().getExperience();
        player.getData().setExperience(experience + xp);
        int nextLevelXP = getXPForNextLevel(player.getLevel());
        boolean isLevelUp = false;

        while (experience >= nextLevelXP) {
            int level = player.getLevel();
            player.setLevel(level + 1);
            nextLevelXP = getXPForNextLevel(player.getLevel());
            isLevelUp = true;
        }
        if (!isLevelUp) {
            return;
        }
        PlayerLevelUpEvent event = new PlayerLevelUpEvent(player, player.getLevel());
        Bukkit.getPluginManager().callEvent(event);
    }

    private static int experienceToLevel(CTFPlayer player) {
        int experience = player.getData().getExperience();
        int tempLevel = 1;

        while (experience >= getXPForNextLevel(tempLevel)) {
            tempLevel++;
        }
        return tempLevel;
    }


}
