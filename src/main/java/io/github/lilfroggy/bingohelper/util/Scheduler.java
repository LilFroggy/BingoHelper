package io.github.lilfroggy.bingohelper.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Scheduler {
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
}