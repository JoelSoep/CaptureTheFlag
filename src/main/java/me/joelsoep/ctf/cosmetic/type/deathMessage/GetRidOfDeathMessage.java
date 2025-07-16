package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import org.jetbrains.annotations.NotNull;

public class GetRidOfDeathMessage extends DeathMessageCosmetic {

    public GetRidOfDeathMessage() {
        super("GetRidOf", CosmeticRarity.RARE);
    }

    @Override
    protected @NotNull String getMessage() {
        return "%k <gray>decided to get rid of %p<gray>!";
    }
}
