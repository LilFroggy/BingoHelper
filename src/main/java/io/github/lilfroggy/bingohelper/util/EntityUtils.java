package io.github.lilfroggy.bingohelper.util;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    @SuppressWarnings({ "unchecked" })
    public static List<Entity> getAllEntitiesOfType(String type) {
        ClientLevel world = CLIENT.level;
        LocalPlayer player = CLIENT.player;
        if (world == null || player == null) return List.of();

        Optional<EntityType<?>> typeOpt = EntityType.byString(type);
        if (typeOpt.isEmpty()) return List.of();
    
        return (List<Entity>) world.getEntities(typeOpt.get(), player.getBoundingBox().inflate(100), entity -> true);
    }

    public static List<AbstractClientPlayer> getPlayersWithSkin(String skin) {
        ClientLevel world = CLIENT.level;
        if (world == null) return List.of();
    
        return world.players().stream()
            .filter(player -> getPlayerSkin(player).equals(skin))
            .toList();
    }

    public static String getPlayerSkin(AbstractClientPlayer player) {
        return player.getGameProfile().properties().get("textures").stream()
            .findFirst()
            .map(property -> property.value())
            .orElse("");
    }

    public static Vec3 getEntityMid(Entity entity) {
        return entity.position().add(0, entity.getBbHeight() / 2, 0);
    }
}