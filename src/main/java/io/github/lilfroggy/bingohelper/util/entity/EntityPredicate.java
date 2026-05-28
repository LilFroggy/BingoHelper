package io.github.lilfroggy.bingohelper.util.entity;

import io.github.lilfroggy.bingohelper.util.EntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

public class EntityPredicate {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private String type;
    private Vec3d position;
    private String skin;

    public EntityPredicate delegate;
    public transient Entity closest;
    
    public transient Set<Entity> cache;
    public int refCount;

    public EntityPredicate(String type, Vec3d position, String skin) {
        this.type = type;
        this.position = position;
        this.skin = skin;
    }

    public void init() {
        cache = new HashSet<>();
    }

    public void register() {
        if (delegate == null) {
            init();
            this.delegate = EntityRegistry.getOrCreate(this);
        }
    }

    public void unregister() {
        if (delegate != null) {
            EntityRegistry.release(delegate);
            delegate = null;
        }
    }

    public EntityPredicate getDelegateOrSelf() {
        return delegate != null ? delegate : this;
    }

    public Set<Entity> getMatches() {
        return getDelegateOrSelf().cache;
    }

    @Nullable
    public Entity getClosest() {
        return getDelegateOrSelf().closest;
    }

    public boolean hasMatch() {
        return !getDelegateOrSelf().cache.isEmpty();
    }

    public void scanWorld() {
        cache.clear();
        if (!(CLIENT.world instanceof ClientWorld world)) return;
        if (!(CLIENT.player instanceof ClientPlayerEntity player)) return;

        closest = null;
        double nearestSq = Double.MAX_VALUE;

        for (Entity entity : world.getEntities()) {
            if (matches(entity)) {
                cache.add(entity);
                
                double distanceSq = player.squaredDistanceTo(entity);
                
                if (distanceSq < nearestSq) {
                    nearestSq = distanceSq;
                    closest = entity;
                }
            }
        }
    }

    public boolean matches(Entity entity) {
        return isType(entity) && hasSkin(entity) && isAt(entity) && canSee(entity) && entity.isAlive();
    }

    private boolean isType(Entity entity) {
        return type == null || entity.getType().getName().getString().toUpperCase().equals(type.toUpperCase());
    }

    private boolean isAt(Entity entity) {
        return position == null || entity.getEntityPos().equals(position);
    }

    private boolean hasSkin(Entity entity) {
        if (skin == null) return true;
        return entity instanceof AbstractClientPlayerEntity player && skin.equals(EntityUtils.getPlayerSkin(player));
    }
    private boolean canSee(Entity entity) {
        return CLIENT.player != null && CLIENT.player.canSee(entity) && !entity.isInvisible();
    }

    public void incrementRef() {
        refCount++;
    }

    public void decrementRef() {
        refCount--;
    
    }
    public int getRefCount() {
        return refCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityPredicate that)) return false;
        return Objects.equals(type, that.type) && Objects.equals(position, that.position) && Objects.equals(skin, that.skin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, position, skin);
    }
}