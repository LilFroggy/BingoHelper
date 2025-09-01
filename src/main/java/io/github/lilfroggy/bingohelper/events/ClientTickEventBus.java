package io.github.lilfroggy.bingohelper.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientTickEventBus {

    public static int currentTick = 0;

    public interface ClientTickListener {
        void onClientTick(int tick);
    }

    private static final EventBus<ClientTickListener> BUS = new EventBus<>();

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (ClientTickListener listener : BUS.getListeners()) {
                listener.onClientTick(currentTick);
            }
            currentTick++;
        });
    }

    public static void register(ClientTickListener listener) {
        BUS.register(listener);
    }

    public static void unregister(ClientTickListener listener) {
        BUS.unregister(listener);
    }
}