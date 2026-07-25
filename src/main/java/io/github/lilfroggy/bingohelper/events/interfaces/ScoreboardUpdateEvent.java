package io.github.lilfroggy.bingohelper.events.interfaces;

import java.util.List;

public interface ScoreboardUpdateEvent {
    void onScoreboardUpdate(List<String> lines);
}