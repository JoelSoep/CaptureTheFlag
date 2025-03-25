package me.joelsoep.ctf.player.effect;

import me.joelsoep.ctf.player.CTFPlayer;
import me.joelsoep.ctf.player.effect.effects.BreakEffect;
import me.joelsoep.ctf.player.effect.effects.ToppleEffect;

public enum EffectType {
    BREAK {

        @Override
        public BreakEffect getEffect() {
            return new BreakEffect();
        }

        @Override
        public void removeEffect(CTFPlayer player) {
            player.getEffects().forEach(playerEffect -> {
                if (!(playerEffect instanceof BreakEffect breakEffect)) {
                    return;
                }
                player.removeEffect(breakEffect);
            });
        }
    },

    TOPPLE {

        @Override
        public ToppleEffect getEffect() {
            return new ToppleEffect();
        }

        @Override
        public void removeEffect(CTFPlayer player) {
            player.getEffects().forEach(playerEffect -> {
                if (!(playerEffect instanceof ToppleEffect toppleEffect)) {
                    return;
                }
                player.removeEffect(toppleEffect);
            });
        }
    };

    public abstract PlayerEffect getEffect();
    public abstract void removeEffect(CTFPlayer player);
}
