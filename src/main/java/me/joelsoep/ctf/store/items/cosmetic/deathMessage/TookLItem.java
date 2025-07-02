package me.joelsoep.ctf.store.items.cosmetic.deathMessage;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.store.StoreCosmeticItem;

public class TookLItem extends StoreCosmeticItem {
    public TookLItem() {
        super("cosmetic.death_message.TookL", Main.getCosmeticsManager().getCosmetic(CosmeticType.DEATH_MESSAGE, "TookL"), 100, 1);

    }
}
