package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.gui.screen.Screen;

public interface CloseScreenEvent {
    void onScreenClose(Screen screen);
}