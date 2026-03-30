package me.joelsoep.ctf.util;

public class IntUtils {

    public static boolean isEqualOrAdjacent(int n, int m) {
        return (n == m || n == m - 1 || n == m + 1);
    }

    public static boolean isWithinPercentage(int n, int m, double percentage) {
        int difference = Math.abs(n - m);
        double percentageDifference = ((double) difference / Math.max(n, m)) * 100;
        return percentageDifference <= percentage;
    }
}
