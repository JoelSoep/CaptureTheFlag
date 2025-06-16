package me.joelsoep.ctf.economy;

import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.OfflineCTFPlayer;
import org.jetbrains.annotations.NotNull;

public class EconomyManager {

    /**
     * Increments the player's total amount of coins.
     *
     * @param player The player which is impacted
     * @param amount The amount of coins to increment
     * @return Whether the payment succeeded. If the amount given is a negative number, the method returns false.
     */

    public static boolean increment(@NotNull CTFPlayer player, int amount) {
        int coins = player.getData().getCoins();
        if (amount < 0) {
            return false;
        }

        player.getData().setCoins(coins + amount);
        return true;
    }

    /**
     * Increments the player's total amount of coins.
     * @implNote It is recommended to use this method in asynchronous environments only, as database calls are made.
     *
     * @param offlinePlayer The player which is impacted
     * @param amount The amount of coins to increment
     * @return Whether the payment succeeded. If the amount given is a negative number, the method returns false.
     */

    public static boolean increment(@NotNull OfflineCTFPlayer offlinePlayer, int amount) {
        int coins = offlinePlayer.getCoins();
        if (amount < 0) {
            return false;
        }

        offlinePlayer.setCoins(coins + amount);
        if (offlinePlayer.save(true).join()) {
            return true;
        } else {
            offlinePlayer.setCoins(coins);
            return false;
        }
    }

    /**
     * Subtracts the player's total amount of coins.
     *
     * @param player The player which is impacted
     * @param amount The amount of coins to be subtracted
     * @return Whether the transaction succeeded. If the end result is less than 0 or the given amount itself is less than 0, the method returns false.
     */

    public static boolean subtract(@NotNull CTFPlayer player, int amount) {
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
     * Subtracts the player's total amount of coins.
     * @implNote It is recommended to use this method in asynchronous environments only, as database calls are made.
     *
     * @param offlinePlayer The offline player which is impacted
     * @param amount The amount of coins to be subtracted
     * @return Whether the transaction succeeded. If the end result is less than 0 or the given amount itself is less than 0, the method returns false.
     */

    public static boolean subtract(@NotNull OfflineCTFPlayer offlinePlayer, int amount) {
        int coins = offlinePlayer.getCoins();
        if (coins - amount < 0) {
            return false;
        }

        if (amount < 0) {
            return false;
        }

        offlinePlayer.setCoins(coins - amount);
        if (offlinePlayer.save(true).join()) {
            return true;
        } else {
            offlinePlayer.setCoins(coins);
            return false;
        }
    }

    /**
     * Sets the player's total amount of coins.
     *
     * @param player The player which is impacted
     * @param amount The amount of coins to set as the player's amount of coins.
     * @return Whether the balance was updated. If the new balance is less than 0, the method returns false.
     */

    public static boolean set(@NotNull CTFPlayer player, int amount) {
        if (amount < 0) {
            return false;
        }

        player.getData().setCoins(amount);
        return true;
    }

    /**
     * Sets the player's total amount of coins.
     * @implNote It is recommended to use this method in asynchronous environments only, as database calls are made.
     *
     * @param offlinePlayer The offline player which is impacted
     * @param amount The amount of coins to set as the player's amount of coins.
     * @return Whether the balance was updated. If the new balance is less than 0, the method returns false.
     */

    public static boolean set(@NotNull OfflineCTFPlayer offlinePlayer, int amount) {
        int currentAmount = offlinePlayer.getCoins();
        if (amount < 0) {
            return false;
        }

        offlinePlayer.setCoins(amount);
        if (offlinePlayer.save(true).join()) {
            return true;
        } else {
            offlinePlayer.setCoins(currentAmount);
            return false;
        }
    }

    /**
     * Returns the player's balance.
     *
     * @param player The player whose balance is returned.
     * @return The amount of coins a player has.
     */

    public static int get(@NotNull CTFPlayer player) {
        return player.getData().getCoins();
    }

    /**
     * Returns the player's balance.
     *
     * @param offlinePlayer The offline player whose balance is returned.
     * @return The amount of coins a player has.
     */

    public static int get(@NotNull OfflineCTFPlayer offlinePlayer) {
        return offlinePlayer.getCoins();
    }
}
