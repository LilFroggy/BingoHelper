package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.data.Collections;
import io.github.lilfroggy.bingohelper.events.CollectionUpdateEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;

public class CollectionStep extends Step implements CollectionUpdateEventBus.CollectionUpdateListener {
    public String collection;
    public Integer level;

    @Override
    public String additionalInstructionFormatting() {
        return instruction;
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        CollectionUpdateEventBus.register(this);

        // Need initial check
        Integer currentLevel = Collections.getLevel(collection);
        if (currentLevel != null && currentLevel >= level) Guide.advance();
    }

    @Override
    protected void onDeactivate() {
        CollectionUpdateEventBus.unregister(this);
    }

    @Override
    public void onCollectionUpdate(String updatedCollection, Integer previousLevel, Integer newLevel) {
        if (!collection.equals(updatedCollection)) return;
        if (newLevel < level) return;
        Guide.advance();
    }
    
}