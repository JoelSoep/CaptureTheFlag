package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import me.joelsoep.ctf.util.MM;
import org.jetbrains.annotations.NotNull;

public class TookLDeathMessage extends DeathMessageCosmetic {
    public TookLDeathMessage() {
        super("TookL", CosmeticRarity.EPIC);
        this.setDisplayName(MM.deserialize("You took an L!"));
    }

    @Override
    protected @NotNull String getMessage() {
        return "<yellow>%p <gray>took a huge L while he died fighting against <yellow>%k<gray>!";
    }
}
