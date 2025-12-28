package io.github.lilfroggy.bingohelper;

import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.fabricmc.api.ModInitializer;

import net.hypixel.modapi.HypixelModAPI;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class BingoHelper implements ModInitializer {
	public static final String MOD_ID = "bingohelper";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		HypixelModAPI hypixelModAPI = HypixelModAPI.getInstance();
		hypixelModAPI.subscribeToEventPacket(ClientboundLocationPacket.class);
		hypixelModAPI.createHandler(ClientboundLocationPacket.class, Skyblock::onLocationPacket);
	}

}