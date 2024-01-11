package me.joelsoep.ctf.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class GameSound {

    public GameSound(Player player, Sound sound) {
        player.playSound(sound);
    }

    public GameSound(Audience audience, Sound sound) {
        audience.playSound(sound);
    }

    public static Sound getLosingSound() {
        return Sound.sound()
                .type(Key.key("block.amethyst_block.break"))
                .pitch(0.5F)
                .volume(10F)
                .source(Sound.Source.MASTER)
                .seed(7)
                .build();
    }

    public static Sound getDeathSound() {
        return Sound.sound()
                .type(Key.key("entity.player.death"))
                .volume(10F)
                .source(Sound.Source.MASTER)
                .seed(7)
                .build();
    }

    public static Sound getVictorySound() {
        return Sound.sound()
                .type(Key.key("entity.firework_rocket.launch"))
                .volume(10)
                .source(Sound.Source.MASTER)
                .pitch(1)
                .seed(7)
                .build();
    }
}
