package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class EntityUtils {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @SuppressWarnings({ "unchecked" })
    public static List<Entity> getAllEntitiesOfType(String type) {
        ClientWorld world = CLIENT.world;
        ClientPlayerEntity player = CLIENT.player;
        if (world == null || player == null) return List.of();

        Optional<EntityType<?>> typeOpt = EntityType.get(type);
        if (typeOpt.isEmpty()) return List.of();
    
        return (List<Entity>) world.getEntitiesByType(typeOpt.get(), player.getBoundingBox().expand(100), entity -> true);
    }

    public static List<AbstractClientPlayerEntity> getPlayersWithSkin(String skin) {
        ClientWorld world = CLIENT.world;
        if (world == null) return List.of();
    
        return world.getPlayers().stream()
            .filter(player -> getPlayerSkin(player).equals(skin))
            .toList();
    }

    public static String getPlayerSkin(AbstractClientPlayerEntity player) {
        return player.getGameProfile().properties().get("textures").stream()
            .findFirst()
            .map(property -> property.value())
            .orElse("");
    }

    public static Vec3d getEntityMid(Entity entity) {
        return entity.getEntityPos().add(0, entity.getHeight() / 2, 0);
    }
}