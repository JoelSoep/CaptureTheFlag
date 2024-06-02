package me.joelsoep.ctf.cosmetic;

import lombok.Getter;

public enum CosmeticRarity {
    COMMON("<#20bd32>", "<#20bd32>Common"),
    RARE("<#243ac9>", "<#243ac9>Rare"),
    EPIC("<#8416c4>", "<#8416c4>Epic"),
    LEGENDARY("<#FFD700>", "<#FFD700>Legendary"),
    MYTHIC("<gradient:#2DFFEC:#ADF3FD>", "<gradient:#2DFFEC:#ADF3FD>Mythic");

    @Getter private final String color;
    @Getter private final String displayName;

    CosmeticRarity(String color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }
}
