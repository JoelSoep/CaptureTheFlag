package me.joelsoep.ctf.util;

import java.util.concurrent.TimeUnit;

public class Timer {

    public static String formatTimer(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(millis - TimeUnit.MINUTES.toMillis(minutes));
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
