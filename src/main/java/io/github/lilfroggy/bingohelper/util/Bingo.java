package io.github.lilfroggy.bingohelper.util;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

public class Bingo {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final ClientTickEndEvent UPDATE_RANK = Bingo::updateRank;
    private static final Pattern RANK_COLOR_PATTERN = Pattern.compile("§(.)Ⓑ");
    private static final ZoneId EASTERN_TIME = ZoneId.of("America/New_York");

    public static void init() {
        Events.CLIENT_TICK_END.register(UPDATE_RANK);
    }

    public static void updateRank(int tick) {
        if (CLIENT.player == null) return;
    
        String playerName = CLIENT.player.getDisplayName().getString();
        Matcher matcher = RANK_COLOR_PATTERN.matcher(playerName);

        if (!matcher.find()) return;
        
        char colorCode = matcher.group(1).charAt(0);
        Formatting formatting = Formatting.byCode(colorCode);        
        
        if (formatting == null) return;
        
        Config.bingoRank = getRankFromFormatting(formatting);
        Events.CLIENT_TICK_END.unregister(UPDATE_RANK);
        Logger.info("Set bingo rank: " + Config.bingoRank, !Config.debug);
    }

    private static int getRankFromFormatting(Formatting formatting) {
        return switch (formatting) {
            case GRAY -> 0;
            case GREEN -> 1;
            case BLUE -> 2;
            case DARK_PURPLE -> 3;
            case GOLD -> 4;
            case LIGHT_PURPLE -> 5;
            case AQUA -> 6;
            default -> 0;
        };
    }

    public static boolean startsInLessThanXHours(int x) {
        ZonedDateTime now = ZonedDateTime.now(EASTERN_TIME);
    
        ZonedDateTime firstOfNextMonth = now.plusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    
        long hoursUntil = Duration.between(now, firstOfNextMonth).toHours();
    
        return hoursUntil < x;
    }

    public static int rank() {
        return Config.bingoRank;
    }
}