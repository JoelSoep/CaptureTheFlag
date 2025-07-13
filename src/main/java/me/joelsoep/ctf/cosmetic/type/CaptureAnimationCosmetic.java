package me.joelsoep.ctf.cosmetic.type;

import lombok.Getter;
import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.CosmeticRarity;
import org.bukkit.Material;

@Getter
public abstract class CaptureAnimationCosmetic extends Cosmetic {
    private final Animation animation;

    public CaptureAnimationCosmetic(String name, CosmeticRarity rarity) {
        super(name, CosmeticType.CAPTURE_ANIMATION);
        this.setRarity(rarity);
        this.setLogo(Material.FIREWORK_STAR);
        this.animation = getCaptureAnimation();
    }

    protected abstract Animation getCaptureAnimation();
}
