package io.github.lilfroggy.bingohelper.util;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;

public class PlayerUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static List<AbstractClientPlayer> getPlayersWithSkin(String skin) {
        ClientLevel world = CLIENT.level;
        if (world == null) return List.of();
    
        return world.players().stream()
            .filter(player -> getSkin(player).equals(skin))
            .toList();
    }

    public static String getSkin(AbstractClientPlayer player) {
        return player.getGameProfile().properties().get("textures").stream()
            .findFirst()
            .map(property -> property.value())
            .orElse("");
    }
}