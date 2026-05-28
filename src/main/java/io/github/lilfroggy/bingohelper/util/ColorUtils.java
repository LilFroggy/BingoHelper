package io.github.lilfroggy.bingohelper.util;

import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?$");

    public static int hexToInt(String hex, int defaultValue) {
        if (hex == null || !HEX_COLOR_PATTERN.matcher(hex).matches()) {
            return defaultValue;
        }

        try {
            String cleanHex = hex.substring(1);
            long colorLong = Long.parseLong(cleanHex, 16);

            if (cleanHex.length() == 6) {
                return (int) (colorLong | 0xFF000000);
            }

            return (int) colorLong;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}