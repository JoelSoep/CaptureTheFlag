package me.joelsoep.ctf.cosmetic;

import lombok.Getter;

@Getter
public enum CosmeticRarity {
    COMMON("<#20bd32>", "\uuE001"),
    RARE("<#243ac9>", "\uE002"),
    EPIC("<#8416c4>", "\uE003"),
    LEGENDARY("<#FFD700>", "\uE004"),
    MYTHIC("<gradient:#2DFFEC:#ADF3FD>", "\u795E");

    private final String color;
    private final String displayName;

    CosmeticRarity(String color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }
}
