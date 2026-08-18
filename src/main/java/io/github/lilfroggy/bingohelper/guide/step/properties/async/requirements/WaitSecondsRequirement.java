package io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.github.lilfroggy.bingohelper.util.Scheduler;

public class WaitSecondsRequirement implements Requirement {

    private Runnable onChange;
    private Integer waitSeconds;
    private ScheduledFuture<?> scheduledTask;
    private boolean isMet = false;

    public WaitSecondsRequirement(Integer waitSeconds) {
        this.waitSeconds = waitSeconds;
        if (waitSeconds == null || waitSeconds <= 0) {
            this.isMet = true;
        }
    }

    @Override
    public void register(Runnable onChange) {
        if (waitSeconds == null || isMet) return;
        this.onChange = onChange;

        scheduledTask = Scheduler.schedule(() -> {
            isMet = true;
            this.onChange.run();
        }, waitSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void unregister() {
        if (scheduledTask == null) return;
        scheduledTask.cancel(false);
        scheduledTask = null;
    }

    @Override
    public boolean isMet() {
        return isMet;
    }

    @Override
    public String toString() {
        return "WaitSecondsRequirement{" +
                "waitSeconds=" + waitSeconds +
                ", isMet=" + isMet +
                '}';
    }
}