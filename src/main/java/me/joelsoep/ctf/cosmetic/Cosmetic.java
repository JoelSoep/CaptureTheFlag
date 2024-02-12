package me.joelsoep.ctf.cosmetic;

import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.logger.LogModule;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

@Data
public abstract class Cosmetic implements Listener {
    private final String name;
    private final CosmeticType type;
    private CosmeticRarity rarity;
    private Component displayName;

    protected Cosmetic(String name, CosmeticType type) {
        this.name = name;
        this.type = type;
    }

    protected Cosmetic(String name, CosmeticType type, CosmeticRarity rarity) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
    }

    protected Cosmetic(String name, CosmeticRarity rarity, CosmeticType type, Component displayName) {
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.displayName = displayName;
    }
}
