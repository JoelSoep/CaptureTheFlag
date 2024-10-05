package me.joelsoep.ctf.cosmetic;

import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.logger.LogModule;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;

@Data
public abstract class Cosmetic implements Listener {
    private final String name;
    private final CosmeticType type;
    private CosmeticRarity rarity;
    private Component displayName;
    private Material logo;
    private Integer price; // remove this later, price should be a shop-based variable.

    protected Cosmetic(String name, CosmeticType type) {
        this.name = name;
        this.type = type;
        this.logo = Material.POPPY;
        this.price = 0;
    }

    protected Cosmetic(String name, CosmeticType type, CosmeticRarity rarity) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.logo = Material.POPPY;
        this.price = 0;
    }

    protected Cosmetic(String name, CosmeticRarity rarity, CosmeticType type, Component displayName) {
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.displayName = displayName;
        this.logo = Material.POPPY;
        this.price = 0;
    }
}
