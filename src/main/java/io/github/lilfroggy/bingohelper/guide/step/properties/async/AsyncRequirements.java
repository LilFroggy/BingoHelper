package io.github.lilfroggy.bingohelper.guide.step.properties.async;

import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;

public class AsyncRequirements {
    public EntityPredicate entity;

    public void register() {
        if (entity != null) entity.register();
    }

    public void unregister() {
        if (entity != null) entity.unregister();
    }

    public boolean entityExists() {
        return entity == null || entity.hasMatch();
    }

    public boolean areMet() {
        return entityExists();
    }
}