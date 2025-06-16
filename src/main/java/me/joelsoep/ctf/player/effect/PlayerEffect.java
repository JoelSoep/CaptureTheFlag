package me.joelsoep.ctf.player.effect;

import lombok.Data;
import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

@Data
public abstract class PlayerEffect {

    private final @NotNull EnumSet<EffectRules> rules;
    private final EffectType effectType;
    private Long duration;

    protected int taskId = 0;

    /**
     * @param effectType should be unique. Create an effecttype if you wish to register a new effect.
     */

    public PlayerEffect(EffectType effectType) {
        this.effectType = effectType;
        this.rules = getRules();
        this.duration = 0L;
    }

    protected abstract @NotNull EnumSet<EffectRules> getRules();

    protected abstract void effect(CTFPlayer player);

    public void play(CTFPlayer player) {
        effect(player);
        player.addEffect(this);
        if (duration != 0) {
            scheduleCancellation(player, duration);
        }
    }

    public void scheduleCancellation(CTFPlayer player, Long ticks) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                player.removeEffect(PlayerEffect.this);
                cancel();
            }
        }, ticks);
    }

    public void cancel() {
        if (taskId == 0) {
            return;
        }

        Bukkit.getScheduler().cancelTask(taskId);
        taskId = 0;
    }
}

