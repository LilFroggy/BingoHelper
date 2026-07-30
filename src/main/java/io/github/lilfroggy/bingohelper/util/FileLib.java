package io.github.lilfroggy.bingohelper.util;

import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

public class FileLib {

    @Nullable
    public static String read(String path, String fallback) {
        if (!exists(path)) return fallback;
        try {
            return Files.readString(Path.of(path));
        } catch (Exception e) {
            Logger.error("Error reading file at", e);
            return fallback;
        }
    }

    public static boolean exists(String path) {
        try {
            return Files.exists(Path.of(path));
        } catch (Exception e) {
            return false;
        }
    }

    public static void write(String path, String string) {
        try {
            Path p = Path.of(path);
            Path parent = p.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(p, string);
        } catch (Exception e) {
            Logger.error("Error writing to " + path, e);
        }
    }
}