package io.github.lilfroggy.bingohelper.util.entity;

import io.github.lilfroggy.bingohelper.util.PlayerUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntityPredicate {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static enum Line {
        ALL,
        NEAREST,
        NONE;
    }

    private Line line;
    private String type;
    private Vec3 position;
    private String skin;

    public EntityPredicate delegate;
    public transient Entity closest;
    private boolean hasMatch;
    private Consumer<Boolean> callback;
    private Set<Consumer<Boolean>> callbacks = new HashSet<>();
    
    public transient Set<Entity> cache = new HashSet<>();
    public int refCount;

    public EntityPredicate() {}

    public EntityPredicate(Line line, String type, Vec3 position, String skin) {
        this.line = line;
        this.type = type;
        this.position = position;
        this.skin = skin;
    }

    public void register() {
        register(null);
    }

    public void register(Consumer<Boolean> onMatchChange) {
        if (delegate != null) return;
        this.callback = onMatchChange;
        this.delegate = EntityRegistry.getOrCreate(this);
        if (callback != null) delegate.callbacks.add(callback);
    }

    public void unregister() {
        if (delegate == null) return;
        delegate.callbacks.remove(callback);
        EntityRegistry.release(delegate);
        delegate = null;
    }

    public EntityPredicate delegate() {
        return delegate != null ? delegate : this;
    }

    public Set<Entity> getMatches() {
        return delegate().cache;
    }

    @Nullable
    public Entity getClosest() {
        return delegate().closest;
    }

    public boolean hasMatch() {
        return !delegate().cache.isEmpty();
    }

    public void scanWorld() {
        cache.clear();
        if (!(CLIENT.level instanceof ClientLevel world)) return;
        if (!(CLIENT.player instanceof LocalPlayer player)) return;

        closest = null;
        double nearestSq = Double.MAX_VALUE;

        for (Entity entity : world.entitiesForRendering()) {
            if (matches(entity)) {
                cache.add(entity);
                
                double distanceSq = player.distanceToSqr(entity);
                
                if (distanceSq < nearestSq) {
                    nearestSq = distanceSq;
                    closest = entity;
                }
            }
        }

        boolean newState = !cache.isEmpty();
        if (hasMatch == newState) return;

        hasMatch = newState;
        for (var action : callbacks) {
            action.accept(hasMatch);
        }
    }

    public Line line() {
        return line == null ? Line.NEAREST : line;
    }

    public boolean matches(Entity entity) {
        return isType(entity) && hasSkin(entity) && isAt(entity) && canSee(entity) && entity.isAlive();
    }

    private boolean isType(Entity entity) {
        return type == null || entity.getType().getDescription().getString().toUpperCase().equals(type.toUpperCase());
    }

    private boolean isAt(Entity entity) {
        return position == null || entity.position().equals(position);
    }

    private boolean hasSkin(Entity entity) {
        if (skin == null) return true;
        return entity instanceof AbstractClientPlayer player && skin.equals(PlayerUtils.getSkin(player));
    }

    private boolean canSee(Entity entity) {
        return CLIENT.player != null && CLIENT.player.hasLineOfSight(entity) && !entity.isInvisible();
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

    @Override
    public String toString() {
        EntityPredicate actual = delegate();
        return "EntityPredicate{" +
                "type='" + type + '\'' +
                ", position=" + position +
                ", skin='" + skin + '\'' +
                ", line=" + line +
                ", hasMatch=" + actual.hasMatch() +
                ", matchesCount=" + actual.getMatches().size() +
                ", refCount=" + refCount +
                ", callback=" + actual.callback +
                ", callbacks=" + actual.callbacks +
                '}';
    }
}