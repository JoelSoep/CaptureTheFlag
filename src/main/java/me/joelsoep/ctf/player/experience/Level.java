package me.joelsoep.ctf.player.experience;

import me.joelsoep.ctf.game.event.PlayerLevelUpEvent;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Bukkit;

public class Level {

    /**
     * Placeholder for calculations. Players start with 0 experience.
     */

    private static final int BASE_XP = 100;

    /**
     * The exponent used when calculating the experience for the next level.
     */

    private static final double EXPONENT = 1.5;

    /**
     * Get the player's level.
     *
     * @param player The player whose level should be returned.
     * @return The player's current level.
     */

    public static int getLevel(CTFPlayer player) {
        return experienceToLevel(player);
    }

    /**
     * Gets the amount of experience required to level up to the next level.
     * The amount of experience required for a level grows exponentially as the player levels up.
     *
     * @param level The current level.
     * @return The next level's experience requirements.
     */

    public static int getXPForNextLevel(int level) {
        return (int) (BASE_XP * Math.pow(level + 1, EXPONENT));
    }

    /**
     * Grants the player XP.
     * This method loops until the player can't level up anymore,
     * so it does support multiple level-ups at once.
     *
     * @param player The player who should gain experience.
     * @param xp The amount of experience to gain.
     */

    public static void gainExperience(CTFPlayer player, int xp) {
        int oldLevel = experienceToLevel(player);
        int experience = player.getData().getExperience();
        player.getData().setExperience(experience + xp);
        experience = player.getData().getExperience();
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
        PlayerLevelUpEvent event = new PlayerLevelUpEvent(player, oldLevel, player.getLevel());
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Translates the player's experience to it's level.
     *
     * @param player The player's experience to translate
     * @return The player's current level.
     */

    public static int experienceToLevel(CTFPlayer player) {
        Integer experience = player.getData().getExperience();
        int tempLevel = 1;

        if (experience == null) {
            experience = 0;
        }

        while (experience >= getXPForNextLevel(tempLevel)) {
            tempLevel++;
        }
        return tempLevel;
    }

    /**
     * Translates a given amount of experience to a level.
     *
     * @param experience The experience to translate
     * @return The level translated by the experience
     */

    public static int experienceToLevel(int experience) {
        int tempLevel = 1;

        while (experience >= getXPForNextLevel(tempLevel)) {
            tempLevel++;
        }
        return tempLevel;
    }

}
