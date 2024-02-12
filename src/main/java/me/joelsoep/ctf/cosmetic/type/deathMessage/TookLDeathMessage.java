package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import org.jetbrains.annotations.NotNull;

public class TookLDeathMessage extends DeathMessageCosmetic {
    public TookLDeathMessage() {
        super("TookL", CosmeticRarity.COMMON);
    }

    @Override
    protected @NotNull String getMessage() {
        return "<yellow>%p <gray>took a huge L while he died fighting against <yellow>%k<gray>!";
    }
}
