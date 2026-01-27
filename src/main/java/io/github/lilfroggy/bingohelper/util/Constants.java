package io.github.lilfroggy.bingohelper.util;

import io.github.lilfroggy.bingohelper.BingoHelper;
import net.fabricmc.loader.api.FabricLoader;

public class Constants {
    public static final String PREFIX = "§b[BH] ";

    // e.g. "0.4.0-mc1.21.5"
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(BingoHelper.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
    // e.g. "1.21.5"
    public static final String MC_VERSION = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString();
}