package me.joelsoep.ctf.cosmetic.type.listener;

import me.joelsoep.ctf.animation.Animation;
import me.joelsoep.ctf.cosmetic.Cosmetic;
import me.joelsoep.ctf.cosmetic.type.CaptureAnimationCosmetic;
import me.joelsoep.ctf.cosmetic.type.CosmeticType;
import me.joelsoep.ctf.game.event.FlagCaptureEvent;
import me.joelsoep.ctf.player.CTFPlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CaptureAnimationCosmeticListener implements Listener {

    @EventHandler
    public void onCapture(FlagCaptureEvent event) {
        Location flagLocation = event.getCtfPlayer().getTeam().getFlag().getLocation().clone();
        CTFPlayer player = event.getCtfPlayer();

        Cosmetic cosmetic = player.getCosmetics().getAppliedCosmetic(CosmeticType.CAPTURE_ANIMATION);
        if (cosmetic == null) {
            return;
        }

        if (!(cosmetic instanceof CaptureAnimationCosmetic animationCosmetic)) {
            return;
        }

        Animation animation = animationCosmetic.getAnimation();
        animation.setLocation(flagLocation);
        animation.play();
    }
}
