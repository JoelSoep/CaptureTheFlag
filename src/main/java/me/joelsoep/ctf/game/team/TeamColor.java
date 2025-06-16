package me.joelsoep.ctf.game.team;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum TeamColor {
    BLUE("<#084acf>⚐<reset>", "<#084acf>", Material.BLUE_CONCRETE),
    RED("<#cf0826>⚐<reset>", "<#cf0826>", Material.RED_CONCRETE);

    final String prefix;
    final String color;
    final Material material;

    TeamColor(String prefix, String color, Material material) {
        this.prefix = prefix;
        this.color = color;
        this.material = material;
    }
}