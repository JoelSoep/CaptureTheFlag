package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import org.jetbrains.annotations.NotNull;

public class BlownAwayDeathMessage extends DeathMessageCosmetic {

    public BlownAwayDeathMessage() {
        super("BlownAway", CosmeticRarity.COMMON);
    }

    @Override
    protected @NotNull String getMessage() {
        return "%p <gray>was blown away by the coolness of %k<gray>!";
    }
}
