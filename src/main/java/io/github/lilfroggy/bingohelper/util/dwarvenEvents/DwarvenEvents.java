package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import io.github.lilfroggy.bingohelper.Client;
import io.github.lilfroggy.bingohelper.events.EventHandler;
import io.github.lilfroggy.bingohelper.http.HttpUtils;
//import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventEndEvent;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventStartEvent;

public class DwarvenEvents {
    public static final EventHandler<DwarvenEventStartEvent> ON_START = new EventHandler<>();
    public static final EventHandler<DwarvenEventEndEvent> ON_END = new EventHandler<>();

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Gson GSON = new Gson();

    private static final Map<String, Event> active = new LinkedHashMap<>();
    private static final Map<String, ScheduledFuture<?>> expirationTasks = new HashMap<>();
    private static long lastUpdate;

    static {
        SCHEDULER.scheduleAtFixedRate(DwarvenEvents::update, 0, 60, TimeUnit.SECONDS);
    }

    public class SoopyResponse {
        public SoopyData data;
    }
    
    public class SoopyData {
        public EventDatas event_datas;
    }
    
    public class EventDatas {
        public Map<String, Event> DWARVEN_MINES;
    }
    
    public class Event {
        transient public DwarvenEvent type;
        transient public String name;
        transient public String displayName;
        public long starts_at_max;
        public long ends_at_max;

        public String displayTime() {
            long now = System.currentTimeMillis();
            
            if (now < starts_at_max) {
                long remaining = (starts_at_max - now) / 1000;
                return "§a" + formatTime(remaining);
            }
            else if (now < ends_at_max) {
                long remaining = (ends_at_max - now) / 1000;
                return "§6" + formatTime(remaining);
            } 
            else {
                return "§cEnded";
            }
        }

        private String formatTime(long seconds) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            
            if (minutes > 0) {
                return minutes + "m " + remainingSeconds + "s";
            } else {
                return remainingSeconds + "s";
            }
        }

        public String displayString() {
            return displayName + " &r&7(" + displayTime() + "&r&7)"; 
        }

        public boolean isRunning() {
            return System.currentTimeMillis() < ends_at_max;
        }

        public String toString() {
            return "{" +
                    "\n  type: " + type.toString() +
                    "\n  name: " + name +
                    "\n  displayName: " + displayName +
                    "\n  starts_in_max: " + formatTime((starts_at_max - System.currentTimeMillis()) / 1000) +
                    "\n  ends_in_max: " + formatTime((ends_at_max - System.currentTimeMillis()) / 1000) +
                    "\n}";
        }
    }

    public static void update() {
        HttpUtils.sendAsync("https://api.soopy.dev/skyblock/chevents/get", response -> {
            SoopyResponse res = GSON.fromJson(response.body(), SoopyResponse.class);
            Map<String, Event> data = res.data.event_datas.DWARVEN_MINES;
            
            active.keySet().removeIf(name -> {
                boolean ended = data.keySet().stream().noneMatch(key -> key.equals(name));
                if (ended) {
                    ON_END.invoke(listener -> listener.onDwarvenEventEnd(name));
                }
                return ended;
            });

            data.forEach((name, event) -> {
                event.type = DwarvenEvent.fromString(name);
                event.name = name;
                event.displayName = event.type.displayName();

                boolean isNew = !active.containsKey(name);
                if (isNew) {
                    ON_START.invoke(listener -> listener.onDwarvenEventStart(name));
                }

                active.put(name, event);

                scheduleExpiration(event);

                //Logger.debug("DwarvenEvent: " + event.toString());
            });

            lastUpdate = System.currentTimeMillis();
        });
    }

    private static void scheduleExpiration(Event event) {
        cancelExpirationTask(event.name);

        long delay = event.ends_at_max - System.currentTimeMillis();
        if (delay <= 0) {
            endEvent(event.name);
            return;
        }

        ScheduledFuture<?> future = SCHEDULER.schedule(() -> {
            Client.MINECRAFT.execute(() -> {
                endEvent(event.name);
            });
        }, delay, TimeUnit.MILLISECONDS);

        expirationTasks.put(event.name, future);
    }

    private static void cancelExpirationTask(String name) {
        ScheduledFuture<?> existingTask = expirationTasks.remove(name);
        if (existingTask == null) return;
        existingTask.cancel(false);
    }

    private static void endEvent(String name) {
        active.remove(name);
        expirationTasks.remove(name);
        ON_END.invoke(listener -> listener.onDwarvenEventEnd(name));
    }

    public static boolean isActive(String name) {
        if (name == null) return false;
        Event event = active.get(name);
        return event != null && event.isRunning();
    }

    public static String getActive() {
        String display = "Update in: &a" + ((lastUpdate + 60000 - System.currentTimeMillis()) / 1000) + "s&r";
        for (var event : active.values()) {
            display += "\n" + event.toString();
        }
        return display;
    }
}