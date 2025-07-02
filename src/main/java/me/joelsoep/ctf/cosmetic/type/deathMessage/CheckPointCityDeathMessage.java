package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import org.jetbrains.annotations.NotNull;

public class CheckPointCityDeathMessage extends DeathMessageCosmetic {
    public CheckPointCityDeathMessage() {
        super("CheckpointCity", CosmeticRarity.MYTHIC);
    }

    @Override
    protected @NotNull String getMessage() {
        return "%p <gray>was set back to checkpoint city by %k<gray>!";
    }
}
