package io.github.lilfroggy.bingohelper.util;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class Bingo {
    public static boolean startsInLessThanXHours(int x) {
        ZoneId easternZone = ZoneId.of("America/New_York");
        ZonedDateTime now = ZonedDateTime.now(easternZone);
    
        ZonedDateTime firstOfNextMonth = now.plusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    
        long hoursUntil = Duration.between(now, firstOfNextMonth).toHours();
    
        return hoursUntil < x;
    }
}