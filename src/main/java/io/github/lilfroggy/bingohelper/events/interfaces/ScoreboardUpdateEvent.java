package io.github.lilfroggy.bingohelper.events.interfaces;

import java.util.ArrayList;

public interface ScoreboardUpdateEvent {
    void onScoreboardUpdate(ArrayList<String> lines);
}