package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.Main;
import me.joelsoep.ctf.game.GameState;
import me.joelsoep.ctf.game.special.extension.UltraShieldExtension;

public class CleanExpiredUltraShieldsTask implements Runnable {

    @Override
    public void run() {
        if (Main.getGame().getState() != GameState.RUNNING) {
            return;
        }
        UltraShieldExtension.getACTIVE_ANIMATING_SHIELDS().values().removeIf(UltraShieldExtension.UltraShieldData::isExpired);
    }
}
