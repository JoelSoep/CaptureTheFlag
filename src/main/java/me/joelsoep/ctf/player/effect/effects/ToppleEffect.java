package me.joelsoep.ctf.player.effect.effects;

import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.effect.EffectRules;
import me.joelsoep.ctf.player.effect.EffectType;
import me.joelsoep.ctf.player.effect.PlayerEffect;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class ToppleEffect extends PlayerEffect {

    public ToppleEffect() {
        super(EffectType.TOPPLE);
    }

    @Override
    protected @NotNull EnumSet<EffectRules> getRules() {
        return EnumSet.of(EffectRules.CAN_MOVE_ANY);
    }

    @Override
    protected void effect(CTFPlayer player) {

    }
}
