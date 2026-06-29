 package io.github.lilfroggy.bingohelper;

import io.github.lilfroggy.bingohelper.util.Version;
import io.github.lilfroggy.bingohelper.util.PlayerRank;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.resources.Identifier;

public class BingoHelper implements ModInitializer {
	public static final String MOD_ID = "bingohelper";
	public static final String PREFIX = "§b[BH]§r ";
	public static final Version VERSION = new Version(FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString());
	 // new Version("0.4.0-mc1.21.5"); //

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
		HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, Skyblock::onLocationPacket);
		HypixelModAPI.getInstance().createHandler(ClientboundHelloPacket.class, Skyblock::onHelloPacket);
		HypixelModAPI.getInstance().createHandler(ClientboundPlayerInfoPacket.class, PlayerRank::onPlayerInfoPacket);
	}
	
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}