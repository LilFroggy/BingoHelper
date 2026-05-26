package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public interface WorldChangeEvent {
    void onWorldChange(MinecraftClient client, ClientWorld world);
}