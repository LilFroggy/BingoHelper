package io.github.lilfroggy.bingohelper.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class WorldChangeEventBus {
    public interface WorldChangeListener {
        void onWorldChange(MinecraftClient client, ClientWorld world);
    }

    private static final EventBus<WorldChangeListener> BUS = new EventBus<>();

    static {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((MinecraftClient client, ClientWorld world) -> {
            for (WorldChangeListener listener : BUS.getListeners()) {
                listener.onWorldChange(client, world);
            }
        });
    }

    public static void register(WorldChangeListener listener) {
        BUS.register(listener);
    }

    public static void unregister(WorldChangeListener listener) {
        BUS.unregister(listener);
    }
}