package io.github.lilfroggy.bingohelper.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;

public class Scheduler {
    public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "BingoHelper-Scheduler");
        t.setDaemon(true);
        return t;
    });

    public static ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return SCHEDULER.schedule(() -> {
            Minecraft.getInstance().execute(task);
        }, delay, unit);
    }
}