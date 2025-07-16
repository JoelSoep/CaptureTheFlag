package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import org.jetbrains.annotations.NotNull;

public class WipedOutOfExistenceDeathMessage extends DeathMessageCosmetic {

    public WipedOutOfExistenceDeathMessage() {
        super("WipedOutOfExistence", CosmeticRarity.EPIC);
    }

    @Override
    protected @NotNull String getMessage() {
        return "%p <gray>was wiped out of existence by the force of %k<gray>!";
    }
}
