package io.github.lilfroggy.bingohelper.util;

import java.util.regex.Pattern;

public class Version {
    public static final String SEPARATOR = "-mc";
    public static final String SPLITTER = Pattern.quote(SEPARATOR);

    public final String FULL;
    public final String MOD;
    public final String MC;

    public Version(String version) {
        this.FULL = (version == null) ? "UNKNOWN" : version;
        this.MOD = parseModVersion(this.FULL);
        this.MC = parseMcVersion(this.FULL);
    }

    private static String parseModVersion(String fullVersion) {
        if (!fullVersion.contains(SEPARATOR)) return "UNKNOWN";
        return fullVersion.split(SPLITTER)[0];
    }

    private static String parseMcVersion(String fullVersion) {
        String[] parts = fullVersion.split(SPLITTER);
        return parts.length > 1 ? parts[1] : "UNKNOWN";
    }
}