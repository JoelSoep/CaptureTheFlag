package me.joelsoep.ctf.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {

    public SoundUtils(Player player, Sound sound) {
        player.playSound(sound);
    }

    public SoundUtils(Audience audience, Sound sound) {
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
                .pitch(1F)
                .seed(7)
                .build();
    }

    public static Sound getConfirmSound() {
        return Sound.sound()
                .type(Key.key("block.note_block.pling"))
                .volume(10)
                .source(Sound.Source.MASTER)
                .pitch(2F)
                .seed(7)
                .build();
    }

    public static Sound getDenySound() {
        return Sound.sound()
                .type(Key.key("block.note_block.pling"))
                .volume(10)
                .source(Sound.Source.MASTER)
                .pitch(0.5F)
                .seed(7)
                .build();
    }
}
