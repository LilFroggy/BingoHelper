package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public interface WorldChangeEvent {
    void onWorldChange(Minecraft client, ClientLevel world);
}