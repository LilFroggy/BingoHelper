package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import io.github.lilfroggy.bingohelper.http.HttpUtils;

public class DwarvenEvents {
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Gson GSON = new Gson();

    private static Map<String, Event> events = new HashMap<>();

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
        transient public DwarvenEventType type;
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
    }

    public static void update() {
        HttpUtils.sendAsync("https://api.soopy.dev/skyblock/chevents/get", response -> {
            SoopyResponse res = GSON.fromJson(response.body(), SoopyResponse.class);
            Map<String, Event> data = res.data.event_datas.DWARVEN_MINES;
            
            events.clear();
            data.forEach((key, event) -> {
                event.type = DwarvenEventType.valueOf(key);
                event.displayName = event.type.displayName();
                events.put(key, event);
            });
        });
    }

    public static boolean isActive(String event) {
        return event != null && events.containsKey(event) && events.get(event).isRunning();
    }
}