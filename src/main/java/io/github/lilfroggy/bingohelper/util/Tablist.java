package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;

// Shoutout Skyblocker

public class Tablist {
    private static final Pattern ISLAND_REGEX = Pattern.compile("(?:Area|Dungeon): (.*)");

    private static String island;

    private static List<String> lines = new ArrayList<>();

    static {
        Events.CLIENT_TICK_END.register(Tablist::onClientTickEnd);
    }

    public static void init() {
        // Load
    }

    private static void onClientTickEnd(int tick) {
        if(tick % 20 != 0) return;
        update();
        updateIsland();
    }

    private static void update() {
        ClientPacketListener networkHandler = Minecraft.getInstance().getConnection();
        
		if (networkHandler == null) return;

        lines = networkHandler.getOnlinePlayers()
            .stream()
            .map(PlayerInfo::getTabListDisplayName)
            .filter(Objects::nonNull)
            .map(Component::getString)
            .map(String::strip)
            .toList();
    }

    private static void updateIsland() {
        for (String line : getLines()) {
            Matcher matcher = ISLAND_REGEX.matcher(line);

            if (!matcher.matches()) continue;

            String oldIsland = island;
            island = matcher.group(1);
            if (island.equals(oldIsland)) break;
            Events.CHANGE_ISLAND.invoke(listener -> listener.onIslandChange(oldIsland, oldIsland));
            Logger.info("" + oldIsland + " -> " + island, !Config.debug);
            break;
        }
    }

    public static List<String> getLines() {
        return lines;
    }
}