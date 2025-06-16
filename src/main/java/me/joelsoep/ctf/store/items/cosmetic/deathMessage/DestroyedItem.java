package me.joelsoep.ctf.store.items.cosmetic.deathMessage;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.store.StoreCosmeticItem;

public class DestroyedItem extends StoreCosmeticItem {
    public DestroyedItem() {
        super("death_message.destroyed", Main.getCosmeticsManager().getCosmetic(CosmeticType.DEATH_MESSAGE, "destroyed"), 200, 1);
        this.setDisplayName("");
    }
}
