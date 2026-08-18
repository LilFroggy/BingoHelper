package io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements;

import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;

public class EntityRequirement implements Requirement {

    private EntityPredicate entity;

    public EntityRequirement(EntityPredicate entity) {
        this.entity = entity;
    }

    @Override
    public void register(Runnable onChange) {
        if (entity == null) return;
        entity.register(hasMatch -> onChange.run());
    }

    @Override
    public void unregister() {
        ChatLib.chat("unregistering entity requirement");
        if (entity == null) return;
        entity.unregister();
    }

    @Override
    public boolean isMet() {
        return entity == null || entity.hasMatch();
    }

    @Override
    public String toString() {
        return "EntityRequirement{" +
                "entity=" + entity +
                ", isMet=" + isMet() +
                '}';
    }
}