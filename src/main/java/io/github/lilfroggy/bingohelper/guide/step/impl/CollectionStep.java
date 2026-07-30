package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.data.Collections;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.LevelCollectionEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class CollectionStep extends Step implements LevelCollectionEvent {

    public String collection;
    public Integer level;

    @Override
    public String locallyFormatted() {
        return instruction;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        if (Collections.getLevel(collection) >= level) complete(); // Initial check
        Events.LEVEL_COLLECTION.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.LEVEL_COLLECTION.unregister(this);
    }

    @Override
    public void onLevelCollection(String updatedCollection, Integer previousLevel, Integer newLevel) {
        if (!collection.equals(updatedCollection)) return;
        if (newLevel >= level) complete();
        onProgress();
    }
}