package me.joelsoep.ctf.cosmetic.type;

import lombok.Getter;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.CosmeticManager;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.game.Game;
import me.joelsoep.ctf.logger.LogModule;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.PlayerService;
import me.joelsoep.ctf.util.MM;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

public abstract class DeathMessageCosmetic extends Cosmetic {
   @Getter private final String deathMessage;

    protected DeathMessageCosmetic(String name, CosmeticRarity rarity) {
        super(name, CosmeticType.DEATH_MESSAGE);
        this.setRarity(rarity);
        this.deathMessage = getMessage();
    }

    /**
     * The custom death message.
     * The message should always contain 2 placeholders:
     *
     * %k the killer
     * %p the player that died.
     *
     * @return The custom deathMessage as a string.
     */

    protected abstract @NotNull String getMessage();
}
