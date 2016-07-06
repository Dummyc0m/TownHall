package com.dummyc0m.bukkit.townhall.core.util;

import java.util.Random;

/**
 * Created by Dummyc0m on 3/3/15.
 * Requires testinggam
 */
public class MathUtil {
    private static final float[] SIN_TABLE = new float[65536];
    private static final Random random = new Random();

    static {
        for (int var0 = 0; var0 < 65536; ++var0) {
            SIN_TABLE[var0] = (float) Math.sin((double) var0 * Math.PI * 2.0D / 65536.0D);
        }
    }

    private MathUtil() {
        //UTIL CLASS
    }

    public static int random(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static double random(double min, double max) {
        return (random.nextDouble() + +Double.MIN_VALUE) * (max - min) + min;
    }

    public static float sin(float theta) {
        return SIN_TABLE[(int) (theta * 10430.378F) & 65535];
    }

    public static float cos(float theta) {
        return SIN_TABLE[(int) (theta * 10430.378F + 16384.0F) & 65535];
    }

    public static float abs(float value) {
        return value >= 0.0F ? value : -value;
    }

    public static int abs_int(int value) {
        return value >= 0 ? value : -value;
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
