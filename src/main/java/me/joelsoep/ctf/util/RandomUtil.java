package me.joelsoep.ctf.util;

import java.util.Random;

public class RandomUtil {
    private static final Random random = new Random();

    public static Random get() {
        return random;
    }

}
