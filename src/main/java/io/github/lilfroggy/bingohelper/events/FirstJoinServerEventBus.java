package io.github.lilfroggy.bingohelper.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

public class FirstJoinServerEventBus {

    public interface FirstJoinServerListener {
        void onFirstJoinServer(MinecraftClient client);
    }
    
    private static final EventBus<FirstJoinServerListener> BUS = new EventBus<>();
    
    private static boolean hasJoined = false;

    static {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.isInSingleplayer() || hasJoined) return;
            
            for (FirstJoinServerListener listener : BUS.getListeners()) {
                listener.onFirstJoinServer(client);
            }
            hasJoined = true;
        });
    }

    public static void register(FirstJoinServerListener listener) {
        BUS.register(listener);
    }
    
    public static void unregister(FirstJoinServerListener listener) {
        BUS.unregister(listener);
    }
}