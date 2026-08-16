package io.github.lilfroggy.bingohelper.util;

import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.jetbrains.annotations.Nullable;

import io.github.lilfroggy.bingohelper.BingoHelper;

public class ChatLib {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    // Command Overloads
    public static void chatClickableCommand(String message, String command) {
        chatClickableCommand(message, command, "/" + command, true);
    }

    public static void chatClickableCommand(String message, String command, boolean withPrefix) {
        chatClickableCommand(message, command, "/" + command, withPrefix);
    }

    public static void chatClickableCommand(String message, String command, String hoverText) {
        chatClickableCommand(message, command, hoverText, true);
    }

    public static void chatClickableCommand(String message, String command, String hoverText, boolean withPrefix) {
        chatClickable(message, new ClickEvent.RunCommand(command), hoverText, withPrefix);
    }

    // URL Overloads
    public static void chatClickableUrl(String message, String url) {
        chatClickableUrl(message, url, url, true);
    }

    public static void chatClickableUrl(String message, String url, boolean withPrefix) {
        chatClickableUrl(message, url, url, withPrefix);
    }

    public static void chatClickableUrl(String message, String url, String hoverText) {
        chatClickableUrl(message, url, hoverText, true);
    }

    public static void chatClickableUrl(String message, String url, String hoverText, boolean withPrefix) {
        chatClickable(message, new ClickEvent.OpenUrl(URI.create(url)), hoverText, withPrefix);
    }

    // Clickable Helper
    private static void chatClickable(String message, ClickEvent clickEvent, String hoverText, boolean withPrefix) {
        Component msg = Component.literal(message)
            .withStyle(style -> style
                .withClickEvent(clickEvent)
                .withHoverEvent(new HoverEvent.ShowText(Component.literal(hoverText)))
            );
        chat(msg, withPrefix);
    }

    // Main Chat Methods
    public static void chatNoPrefix(String message) { chat(message, false); }
    public static void chatNoPrefix(Component message) { chat(message, false); }
    public static void chat(String message) { chat(message, true); }
    public static void chat(Component message) { chat(message, true); }

    public static void chat(String message, boolean withPrefix) {
        chat(Component.literal(message), withPrefix);
    }

    public static void chat(Component message, boolean withPrefix) {
        final Component FINAL = withPrefix ? Component.literal(BingoHelper.PREFIX).append(message) : message;

        try {
            CLIENT.schedule(() -> CLIENT.gui.getChat().addClientSystemMessage(FINAL));
        } catch (Exception e) {
            Logger.error("Error sending chat message", e, true);
        }
    }

    /**
     * Sends a command to the server.
     * @param command The command to send/execute
     */
    public static void command(String command) {
        if (command == null || command.isEmpty()) return;
        if (!(CLIENT.player instanceof LocalPlayer player)) return;
        String sanitized = command.startsWith("/") ? command.substring(1) : command;
        player.connection.sendCommand(sanitized);
    }

    /**
     * Shows a title to the player.
     * @param title The main title text
     * @param subtitle The subtitle text (can be null)
     * @param fadeIn Fade in time in ticks (20 ticks = 1 second)
     * @param stay Stay time in ticks
     * @param fadeOut Fade out time in ticks
     */
    public static void showTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (!(CLIENT.gui instanceof Gui hud)) return;

        hud.setTimes(fadeIn, stay, fadeOut);
    
        if (subtitle != null) {
            hud.setSubtitle(Component.literal(replaceAmpersands(subtitle)));
        }
    
        if (title != null) {
            hud.setTitle(Component.literal(replaceAmpersands(title)));
        }
    }

    /**
     * Shows a simple title with default timing.
     * @param title The main title text
     * @param subtitle The subtitle text (can be null)
     */
    public static void showTitle(String title, String subtitle) {
        showTitle(title, subtitle, 10, 40, 10);
    }

    /**
     * Shows a title with only the main text.
     * @param title The main title text
     */
    public static void showTitle(String title) {
        showTitle(title, null);
    }

    public static String formatSeconds(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;

        StringBuilder timeString = new StringBuilder();
        if (h > 0) {
            timeString.append(h).append("h ");
        }
        if (m > 0 || h > 0) {
            timeString.append(m).append("m ");
        }
        timeString.append(s).append("s");

        return timeString.toString().trim();
    }

    /**
     * Converts Title Case to snake_case.
     * E.g. "Green Thumb" -> "green_thumb", "Zooming" -> "zooming"
     */
    @Nullable
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.toLowerCase().replaceAll(" ", "_");
    }

    /**
     * Converts a snake_case to Title Case.
     * E.g. "green_thumb" -> "Green Thumb", "zooming" -> "Zooming"
     */
    @Nullable
    public static String toTitleCase(String input) {
        if (input == null) return null;
        String[] words = input.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) continue;
            sb.append(Character.toUpperCase(words[i].charAt(0)));
            if (words[i].length() > 1) {
                sb.append(words[i].substring(1).toLowerCase());
            }
            if (i < words.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Decodes a roman numeral into its respective number. Eg VII -> 7, LII -> 52 etc.
     * If the string can be parsed as an integer, returns that instead.
     * Returns null if the numeral is invalid.
     * Supported symbols: I, V, X, L, C, D, M
     * @param numeral The roman numeral string to decode
     * @return The decoded number, or null if invalid
     */
    public static Integer decodeNumeral(String numeral) {
        if (numeral == null) return null;
        
        try {
            return Integer.parseInt(numeral.trim());
        } catch (NumberFormatException e) {}
        
        if (!numeral.matches("^[IVXLCDM]+$")) return null;
        
        int[] values = new int[128]; // ASCII table size for character indexing
        values['I'] = 1;
        values['V'] = 5;
        values['X'] = 10;
        values['L'] = 50;
        values['C'] = 100;
        values['D'] = 500;
        values['M'] = 1000;
        
        int sum = 0;
        for (int i = 0; i < numeral.length(); i++) {
            int curr = values[numeral.charAt(i)];
            int next = (i < numeral.length() - 1) ? values[numeral.charAt(i + 1)] : 0;
            
            if (curr < next) {
                sum += next - curr;
                i++; // Skip the next character since we've already used it
                continue;
            }
            sum += curr;
        }
        
        return sum;
    }

    public static double parseKMB(String text) {
        if (text == null || text.isEmpty()) return 0.0;
        
        String clean = text.replace(",", "").toLowerCase().trim();
        if (clean.isEmpty()) return 0.0;
        
        double multiplier = 1.0;
        char lastChar = clean.charAt(clean.length() - 1);
        
        if (lastChar == 'k') {
            multiplier = 1_000.0;
            clean = clean.substring(0, clean.length() - 1);
        } else if (lastChar == 'm') {
            multiplier = 1_000_000.0;
            clean = clean.substring(0, clean.length() - 1);
        } else if (lastChar == 'b') {
            multiplier = 1_000_000_000.0;
            clean = clean.substring(0, clean.length() - 1);
        }

        try {
            return Double.parseDouble(clean) * multiplier;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static String removeFormatting(String string) {
        return string.replaceAll("[&§][0-9a-fk-or]", "");
    }

    public static String replaceAmpersands(String string) {
        return string.replaceAll("&([0-9a-fk-or])", "§$1");
    }

    public static String toBold(String string) {
        return "§l" + string
            .replaceAll("([&§][0-9a-fk-or])", "$0§l")
            .replaceAll("[&§]r", "§f");
    }
}