package me.joelsoep.ctf.player.effect.effects;

import me.joelsoep.ctf.animation.animation.BreakAnimation;
import me.joelsoep.ctf.player.effect.EffectType;
import me.joelsoep.ctf.player.effect.PlayerEffect;
import me.joelsoep.ctf.player.effect.EffectRules;
import me.joelsoep.ctf.util.MM;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BreakEffect extends PlayerEffect {
    public BreakEffect() {
        super(EffectType.BREAK);
    }

    @Override
    protected @NotNull Set<EffectRules> getRules() {
        return Set.of(EffectRules.CAN_DAMAGE, EffectRules.CAN_MOVE_LOCATION);
    }

    @Override
    protected void effect(Player player) {
        BreakAnimation breakAnimation = new BreakAnimation(player.getEyeLocation());
        breakAnimation.setDuration(5 * 20L);
        breakAnimation.play(0L, 1L);

        player.sendActionBar(MM.deserialize("<light_purple>You have been broken!"));
    }
}
