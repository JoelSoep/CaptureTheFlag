package me.joelsoep.ctf.cosmetic;

import lombok.Data;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.List;

@Data
public abstract class Cosmetic implements Listener {
    private final String name;
    private final CosmeticType type;
    private CosmeticRarity rarity;
    private Component displayName;
    private List<Component> lore;
    private Material logo;

    protected Cosmetic(String name, CosmeticType type) {
        this.name = name;
        this.type = type;
        this.logo = Material.POPPY;
    }

    protected Cosmetic(String name, CosmeticType type, CosmeticRarity rarity) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.logo = Material.POPPY;
    }

    protected Cosmetic(String name, CosmeticRarity rarity, CosmeticType type, Component displayName) {
        this.name = name;
        this.rarity = rarity;
        this.type = type;
        this.displayName = displayName;
        this.logo = Material.POPPY;
    }
}
