package io.github.lilfroggy.bingohelper.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jetbrains.annotations.Nullable;

public class FileLib {
    @Nullable
    public static String read(String path) {
        return read(Paths.get(path));
    }

    @Nullable
    public static String read(Path path) {
        try {
            return Files.readString(path);
        } catch (Exception e) {
            Logger.error("Error reading file", e);
            return null;
        }
    }

    public static boolean exists(String path) {
        try {
            return Files.exists(Paths.get(path));
        } catch (Exception e) {
            return false;
        }
    }
}