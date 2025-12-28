package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;

public class ChatLib {
    public static void chatClickableWithPrefix(String message, String command, String hoverText) {
        Text msg = Text.literal(Constants.PREFIX)
            .formatted(Formatting.BLUE, Formatting.BOLD)
            .styled(style -> style
                .withClickEvent(new ClickEvent.RunCommand(command))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal(hoverText)))
            )
            .append(Text.literal(message).formatted(Formatting.WHITE));

        ChatLib.chat(msg);
    }

    public static void chatWithPrefix(String msg) {
        ChatLib.chat(Constants.PREFIX + msg);
    }

    public static void chat(String msg) {
        chat(Text.of(msg));
    }

    public static void chat(Text msg) {
        try {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(msg);
        } catch (Exception e) {
            Logger.error("Error sending chat message", e, true);
        }
    }

    /**
     * Sends a command to the server or executes it client-side.
     * @param command The command to send/execute
     * @param clientSide Whether to execute as a client-side command (true) or send to server (false)
     */
    public static void command(String command, boolean clientSide) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        
        if (player == null) return;
        
        if (clientSide) {
            // Execute as client-side command
            client.getNetworkHandler().sendCommand(command);
        } else {
            // Send to server
            player.networkHandler.sendChatMessage(command);
        }
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) return;
        
        // Set title timing
        client.getNetworkHandler().onTitleFade(new TitleFadeS2CPacket(fadeIn, stay, fadeOut));
        
        // Show main title
        if (title != null) {
            client.getNetworkHandler().onTitle(new TitleS2CPacket(Text.of(title)));
        }
        
        // Show subtitle
        if (subtitle != null) {
            client.getNetworkHandler().onSubtitle(new SubtitleS2CPacket(Text.of(subtitle)));
        }
    }

    /**
     * Shows a simple title with default timing.
     * @param title The main title text
     * @param subtitle The subtitle text (can be null)
     */
    public static void showTitle(String title, String subtitle) {
        showTitle(title, subtitle, 10, 40, 10); // 0.5s fade in, 2s stay, 0.5s fade out
    }

    /**
     * Shows a title with only the main text.
     * @param title The main title text
     */
    public static void showTitle(String title) {
        showTitle(title, null);
    }

    public static String formatDuration(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder timeString = new StringBuilder();
        if (hours > 0) {
            timeString.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            timeString.append(minutes).append("m ");
        }
        timeString.append(seconds).append("s");

        return timeString.toString().trim();
    }

    /**
     * Converts Title Case to snake_case.
     * E.g. "Green Thumb" -> "green_thumb", "Zooming" -> "zooming"
     */
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.toLowerCase().replaceAll(" ", "_");
    }

    /**
     * Converts a snake_case to Title Case.
     * E.g. "green_thumb" -> "Green Thumb", "zooming" -> "Zooming"
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
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
        
        // Check if the string contains only valid roman numeral characters
        if (!numeral.matches("^[IVXLCDM]+$")) return null;
        
        // Roman numeral values mapping
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
        return string.replaceAll("§[0-9a-fk-or]", "");
    }
}