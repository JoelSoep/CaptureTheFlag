package me.joelsoep.ctf.player.effect.effects;

import me.joelsoep.ctf.animation.animations.BreakAnimation;
import me.joelsoep.ctf.game.board.ActionbarPriority;
import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.effect.EffectRules;
import me.joelsoep.ctf.player.effect.EffectType;
import me.joelsoep.ctf.player.effect.PlayerEffect;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class BreakEffect extends PlayerEffect {
    public BreakEffect() {
        super(EffectType.BREAK);
    }

    @Override
    protected @NotNull EnumSet<EffectRules> getRules() {
        return EnumSet.of(EffectRules.CAN_DAMAGE, EffectRules.CAN_MOVE_LOCATION);
    }

    @Override
    protected void effect(CTFPlayer player) {
        BreakAnimation breakAnimation = new BreakAnimation(player.getPlayer().getEyeLocation());
        breakAnimation.setDuration(5 * 20L);
        breakAnimation.play(0L, 1L);

        player.getActionbarHandler().setMessage(ActionbarPriority.HIGH, "<light_purple>You have been broken!", 5 * 20L);
    }
}
