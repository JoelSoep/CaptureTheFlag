package me.joelsoep.ctf.util;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KillStreakHandler {
    private final CTFPlayer player;
    private final Game game;

    public KillStreakHandler(CTFPlayer player) {
        this.player = player;
        this.game = Main.getGame();
        sendMessage();
        grantReward();
    }

    public void sendMessage() {
        switch (player.getKillStreak()) {
            case 3: message("<yellow>Killing Spree! %s <gray>is on a roll with a 3-kill streak!"); return;
            case 5: message("<green>Dominating! %s <gray>is dominating the battlefield with a 5-kill streak!"); return;
            case 7: message("<red><bold>UNSTOPPABLE! </bold>%s <gray>is unstoppable - a 7-kill streak!"); return;
            case 10: message("<aqua><bold>INSANITY! </bold>%s <gray>is on a 10-kill streak! Eliminate him!"); return;
            default: return;
        }
    }

    public void grantReward() {
        switch (player.getKillStreak()) {
            case 3, 7: player.getPlayer().getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE)); return;
            case 5: player.getPlayer().getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE)); return;
            case 10: player.getPlayer().getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS)); return;
            default: return;
        }
    }

    public void message(String input) {
        game.getAudience().sendMessage(MM.deserialize(input.formatted(getFormatted())));
    }

    public String getFormatted() {
        return player.getTeam().getTeamColor().getColor() + player.getPlayer().getName();
    }

}
