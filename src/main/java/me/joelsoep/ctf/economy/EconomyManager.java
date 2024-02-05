package me.joelsoep.ctf.economy;

import me.joelsoep.ctf.player.CTFPlayer;

public class EconomyManager {

    /**
     * Increments the player's total amount of coins.
     *
     * @param player The player which is impacted
     * @param amount The amount of coins to increment
     * @return Whether the payment succeeded. If the amount given is a negative number, the method returns false.
     */

    public static boolean increment(CTFPlayer player, int amount) {
        int coins = player.getData().getCoins();
        if (amount < 0) {
            return false;
        }

        player.getData().setCoins(coins + amount);
        return true;
    }

    /**
     * Subtracts the player's total amount of coins.
     *
     * @param player The player which is impacted
     * @param amount The amount of coins to be subtracted
     * @return Whether the transaction succeeded. If the end result is less than 0 or the given amount itself is less than 0, the method returns false.
     */

    public static boolean subtract(CTFPlayer player, int amount) {
        int coins = player.getData().getCoins();
        if (coins - amount < 0) {
            return false;
        }

        if (amount < 0) {
            return false;
        }

        player.getData().setCoins(coins - amount);
        return true;
    }

    /**
     * Sets the player's total amount of coins.
     *
     * @param player The player which is impacted
     * @param amount The amount of coins to set as the player's amount of coins.
     * @return Whether the balance was updated. If the new balance is less than 0, the method returns false.
     */

    public static boolean set(CTFPlayer player, int amount) {
        if (amount < 0) {
            return false;
        }
        player.getData().setCoins(amount);
        return true;
    }

    /**
     * Returns the player's balance.
     *
     * @param player The player whose balance is returned.
     * @return The amount of coins a player has.
     */

    public static int get(CTFPlayer player) {
        return player.getData().getCoins();
    }
}
