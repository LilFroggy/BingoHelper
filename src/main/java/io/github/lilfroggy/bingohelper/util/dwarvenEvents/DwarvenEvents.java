package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.Gson;

import io.github.lilfroggy.bingohelper.Client;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.EventHandler;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderHudEvent;
import io.github.lilfroggy.bingohelper.http.HttpUtils;
import io.github.lilfroggy.bingohelper.hud.HudDisplay;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventEndEvent;
import io.github.lilfroggy.bingohelper.util.dwarvenEvents.interfaces.DwarvenEventStartEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DwarvenEvents {
    private static final ClientTickEndEvent CLIENT_TICK_END = DwarvenEvents::onClientTickEnd;
    private static final RenderHudEvent RENDER_HUD = DwarvenEvents::onRenderHud;

    private static final Set<String> participated = new HashSet<>();

    public static final EventHandler<DwarvenEventStartEvent> ON_START = new EventHandler<>();
    public static final EventHandler<DwarvenEventEndEvent> ON_END = new EventHandler<>();

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Gson GSON = new Gson();

    private static final HudDisplay display = new HudDisplay("", "dwarvenEvents", () -> Config.dwarvenEvents);
    private static final Map<String, Event> active = new LinkedHashMap<>();
    private static final Map<String, ScheduledFuture<?>> expirationTasks = new HashMap<>();
    private static final int UPDATE_INTERVAL_MS = 60000;
    private static long lastUpdate;

    static {
        if (Config.dwarvenEvents) {
            Events.CLIENT_TICK_END.register(CLIENT_TICK_END);
            Events.RENDER_HUD.register(RENDER_HUD);
        }

        Config.INSTANCE.registerListener("dwarvenEvents", state -> {
            if ((boolean) state) {
                Events.RENDER_HUD.register(RENDER_HUD);
                Events.CLIENT_TICK_END.register(CLIENT_TICK_END);
            } else {
                Events.RENDER_HUD.unregister(RENDER_HUD);
                Events.CLIENT_TICK_END.unregister(CLIENT_TICK_END);
            }
        });

        Events.MESSAGE.register(DwarvenEvents::onMessage);
        SCHEDULER.scheduleAtFixedRate(DwarvenEvents::update, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public static void update() {
        HttpUtils.sendAsync("https://api.soopy.dev/skyblock/chevents/get", response -> {
            Response res = GSON.fromJson(response.body(), Response.class);
            Map<String, Event> data = res.data.event_datas.DWARVEN_MINES;

            participated.removeIf(key -> !data.containsKey(key));
            
            active.keySet().removeIf(name -> {
                boolean ended = !data.containsKey(name);
                if (ended) ON_END.invoke(listener -> listener.onDwarvenEventEnd(name));
                return ended;
            });

            for (var entry : data.entrySet()) {
                final String name = entry.getKey();
                final Event event = entry.getValue();

                if (participated.contains(name)) continue;

                event.type = Type.of(name);
                event.name = name;
                event.displayName = event.type.displayName();

                boolean isNew = !active.containsKey(name);
                if (isNew) {
                    ON_START.invoke(listener -> listener.onDwarvenEventStart(name));
                }

                active.put(name, event);

                scheduleRemoval(event);

                //Logger.debug("Received Dwarven Event: " + event.toString());
            }

            lastUpdate = System.currentTimeMillis();
        });
    }

    private static void scheduleRemoval(Event event) {
        cancelRemoval(event.name);

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

    private static void cancelRemoval(String name) {
        ScheduledFuture<?> existingTask = expirationTasks.remove(name);
        if (existingTask == null) return;
        existingTask.cancel(false);
    }

    private static void endEvent(String name) {
        active.remove(name);
        expirationTasks.remove(name);
        ON_END.invoke(listener -> listener.onDwarvenEventEnd(name));
        updateDisplay();
    }

    public static boolean isActive(String name) {
        return active.containsKey(name);
    }

    public static boolean isActive(Type type) {
        return active.containsKey(type.name());
    }

    public static void onMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!"Dwarven Mines".equals(Skyblock.area())) return;

        String msg = unformattedMsg.strip();

        for (var type : Type.active()) {
            for (var participationMessage : type.participationMessages()) {
                if (msg.startsWith(participationMessage)) {
                    participated.add(type.name());
                    endEvent(type.name());
                    return;
                }
            }
        }
    }

    public static void onClientTickEnd(int tick) {
        if (tick % 20 != 0) return;
        if (!Config.debug && active.isEmpty()) return;

        updateDisplay();
    }

    public static long secondsTillUpdate() {
        return (lastUpdate + UPDATE_INTERVAL_MS - System.currentTimeMillis()) / 1000;
    }

    private static final StringBuilder body = new StringBuilder();

    public static String getDisplay() {
        body.setLength(0);

        if (Config.debug) {
            body.append("Update in: &a").append(secondsTillUpdate());
            if (!active.isEmpty()) body.append("\n");
        }
        
        String result = active.values().stream()
            .map(Event::displayString)
            .collect(Collectors.joining("\n"));

        body.append(result);

        return body.toString();
    }

    public static void updateDisplay() {
        display.setString(getDisplay());

        Logger.debug("updated dwarven events display");
    }

    public static void onRenderHud(GuiGraphicsExtractor graphics, DeltaTracker tickDelta) {
        display.draw(graphics);
    }
}