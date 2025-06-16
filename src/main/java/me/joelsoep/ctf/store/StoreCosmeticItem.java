package me.joelsoep.ctf.store;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Material;

import java.awt.*;

public class StoreCosmeticItem extends StoreItem {

    @Getter
    private final Cosmetic cosmetic;

    public StoreCosmeticItem(String name, Cosmetic cosmetic, int price, double discount) {
        super(name, price, discount);
        this.cosmetic = cosmetic;
    }

    public StoreCosmeticItem(String name, String displayName, Cosmetic cosmetic, int price, double discount) {
        super(name, price, discount);
        this.cosmetic = cosmetic;
    }

    public CosmeticType getType() {
        return cosmetic.getType();
    }

}
