package io.github.lilfroggy.bingohelper.events.interfaces;

import java.util.List;

public interface TablistUpdateEvent {
    void onTablistUpdate(List<String> lines);
}
