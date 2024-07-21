package me.joelsoep.ctf.util;

public class IntUtils {

    public static boolean isEqualOrAdjacent(int n, int m) {
        return (n == m || n == m - 1 || n == m + 1);
    }

    public static boolean isWithinPercentage(int n, int m, double percentage) {
        // Calculate the absolute difference between the two numbers
        int difference = Math.abs(n - m);
        // Calculate the percentage difference
        double percentageDifference = ((double) difference / Math.max(n, m)) * 100;
        // Check if the percentage difference is within the specified percentage
        return percentageDifference <= percentage;
    }
}
