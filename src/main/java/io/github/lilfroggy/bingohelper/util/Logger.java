package io.github.lilfroggy.bingohelper.util;

import org.slf4j.LoggerFactory;

import io.github.lilfroggy.bingohelper.BingoHelper;

public class Logger {
    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BingoHelper.MOD_ID);

    private static final String ERROR_PREFIX = "§c[BH ERROR] ";
    private static final String WARN_PREFIX = "§e[BH WARN] ";
    private static final String INFO_PREFIX  = "§f[BH INFO] ";

    private static final int MAX_ERROR_LENGTH = 256 - ERROR_PREFIX.length() - 3;

    /* === Error === */
    public static void error(String msg, Exception e) {
        error(msg, e, false);
    }

    public static void error(String msg, Exception e, boolean consoleOnly) {
        LOGGER.error(msg, e);
        if (consoleOnly) return;
        String error = e.toString().split("\n")[0];
        if (error.length() > MAX_ERROR_LENGTH) error = error.substring(0, MAX_ERROR_LENGTH) + "...";
        ChatLib.chat(ERROR_PREFIX + "%s: %s".formatted(msg, error));
    }

    /* === Warn === */
    public static void warn(String msg) {
        warn(msg, false);
    }

    public static void warn(String msg, boolean consoleOnly) {
        LOGGER.warn(ChatLib.removeFormatting(msg));
        if (!consoleOnly) ChatLib.chat(WARN_PREFIX + msg);
    }

    /* === Info === */
    public static void info(String msg) {
        info(msg, false);
    }

    public static void info(String msg, boolean consoleOnly) {
        LOGGER.info(ChatLib.removeFormatting(msg));
        if (!consoleOnly) ChatLib.chat(INFO_PREFIX + msg);
    }
}