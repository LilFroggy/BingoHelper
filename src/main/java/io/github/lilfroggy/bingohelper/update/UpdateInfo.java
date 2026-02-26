package io.github.lilfroggy.bingohelper.update;

import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Version;

public class UpdateInfo {
    private static final String D = " <DIVIDER> "; // Divider
    private static final String S = Pattern.quote(D); // Splitter

    public final UpdateState STATE;
    public final String FILE_NAME;
    public final Version VERSION;
    public final String DOWNLOAD_URL;
    public final String CHANGELOG;

    UpdateInfo(UpdateState state, String fileName, Version version, String downloadUrl, String changelog) {
        this.STATE = state;
        this.FILE_NAME = fileName;
        this.VERSION = version;
        this.DOWNLOAD_URL = downloadUrl;
        this.CHANGELOG = parseChangelog(changelog);
    }

    public String toString() {
        return
        STATE
        +D+
        FILE_NAME
        +D+
        VERSION.FULL
        +D+
        DOWNLOAD_URL
        +D+
        CHANGELOG;
    }

    @Nullable
    public static UpdateInfo fromString(String updateInfo) {
        if (updateInfo == null || updateInfo.isEmpty()) return null;
        try {
            String[] args = updateInfo.split(S);
            return new UpdateInfo(
                UpdateState.fromString(args[0]),
                args[1],
                new Version(args[2]),
                args[3],
                parseChangelog(args[4])
            );
        } catch (Exception e) {
            Logger.error("Bad update info: " + updateInfo, e);
            return null;
        }
    }

    private static String parseChangelog(String input) {
        if (input == null || input.isEmpty()) return "";
        input = input.replaceAll(D, "");
    
        input = input.replace("\r\n", "\n");
        input = input.replaceAll("(?m)^#+\\s*", "§f");
        return input.trim();
    }
}