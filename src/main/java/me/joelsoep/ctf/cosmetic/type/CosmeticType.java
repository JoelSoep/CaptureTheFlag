package me.joelsoep.ctf.cosmetic.type;

import lombok.Getter;
import me.joelsoep.ctf.cosmetic.Cosmetic;

@Getter
public enum CosmeticType {
    //FINISHER("finisher."),
    DEATH_MESSAGE("death_message.", DeathMessageCosmetic.class),
   // BANNER_PATTERN("banner_pattern."),
    CAPTURE_ANIMATION("capture_animation.", CaptureAnimationCosmetic.class);
  //  WIN_ANIMATION("win_animation."),
   // ARMOR_TRIM("armor_trim.");

    private final String prefix;
    private final Class<? extends Cosmetic> classType;

    CosmeticType(String prefix, Class<? extends Cosmetic> classType) {
        this.prefix = prefix;
        this.classType = classType;
    }
}
