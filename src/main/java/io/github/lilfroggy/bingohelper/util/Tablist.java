package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class Tablist {
    private static List<String> lines = new ArrayList<>();

    static {
        ClientTickEventBus.register(Tablist::onClientTick);
    }

    public static void init() {
        // Load
    }

    private static void onClientTick(int tick) {
        if(tick % 20 != 0) return;
        update();
    }

    private static void update() {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        
		if (networkHandler == null) return;

        lines = networkHandler.getPlayerList()
            .stream()
            .map(PlayerListEntry::getDisplayName)
            .filter(Objects::nonNull)
            .map(Text::getString)
            .map(String::strip)
            .toList();
    }

    public static List<String> getLines() {
        return lines;
    }
}