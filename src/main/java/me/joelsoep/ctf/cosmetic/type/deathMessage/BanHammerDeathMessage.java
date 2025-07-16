package me.joelsoep.ctf.cosmetic.type.deathMessage;

import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import me.joelsoep.ctf.cosmetic.type.DeathMessageCosmetic;
import org.jetbrains.annotations.NotNull;

public class BanHammerDeathMessage extends DeathMessageCosmetic {

    public BanHammerDeathMessage() {
        super("BanHammer", CosmeticRarity.RARE);
    }

    @Override
    protected @NotNull String getMessage() {
        return "%p <gray>was slain with the ban hammer by %k<gray>!";
    }
}
