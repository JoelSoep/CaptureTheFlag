package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import me.joelsoep.ctf.util.MM;
import org.jetbrains.annotations.NotNull;

public class DestroyedDeathMessage extends DeathMessageCosmetic {
    public DestroyedDeathMessage() {
        super("destroyed", CosmeticRarity.COMMON);
        this.setDisplayName(MM.deserialize("Destroyer"));
    }

    @Override
    protected @NotNull String getMessage() {
        return "<yellow>%p <gray>was absolutely destroyed by <yellow>%k<gray>!";
    }
}
