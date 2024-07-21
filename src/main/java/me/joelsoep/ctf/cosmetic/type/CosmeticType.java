package me.joelsoep.ctf.cosmetic.type;

import lombok.Getter;
import me.joelsoep.ctf.cosmetic.Cosmetic;

@Getter
public enum CosmeticType {
    //FINISHER("finisher_"),
    DEATH_MESSAGE("death_message_", DeathMessageCosmetic.class);
   // BANNER_PATTERN("banner_pattern_"),
  //  WIN_ANIMATION("win_animation_"),
   // ARMOR_TRIM("armor_trim_");

    private final String prefix;
    private final Class<? extends Cosmetic> classType;

    CosmeticType(String prefix, Class<? extends Cosmetic> classType) {
        this.prefix = prefix;
        this.classType = classType;
    }
}
