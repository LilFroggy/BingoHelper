package io.github.lilfroggy.bingohelper.util;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileLib {
    public static String read(String path) {
        try {
            return Files.readString(Paths.get(path));
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