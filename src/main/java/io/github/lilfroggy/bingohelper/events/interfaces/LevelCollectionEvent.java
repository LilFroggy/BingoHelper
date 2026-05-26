package io.github.lilfroggy.bingohelper.events.interfaces;

public interface LevelCollectionEvent {
    void onLevelCollection(String collection, Integer previousLevel, Integer newLevel);
}