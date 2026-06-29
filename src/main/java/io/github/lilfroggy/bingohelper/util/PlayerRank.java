package io.github.lilfroggy.bingohelper.util;

import io.github.lilfroggy.bingohelper.config.Config;
import net.hypixel.data.rank.MonthlyPackageRank;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;

public class PlayerRank {

    public static void onPlayerInfoPacket(ClientboundPlayerInfoPacket packet) {
        if (Config.debug) Logger.info("packet received: " + packet.toString());
    
        String newRank;
    
        if (packet.getPlayerRank() != net.hypixel.data.rank.PlayerRank.NORMAL) {
            newRank = packet.getPlayerRank().name();
        } else if (packet.getMonthlyPackageRank() != MonthlyPackageRank.NONE) {
            newRank = packet.getMonthlyPackageRank().name();
        } else if (packet.getPackageRank() != null) {
            newRank = packet.getPackageRank().name();
        } else {
            Logger.warn("Failed to parse rank from playerInfo packet");
            return;
        }
    
        if (!newRank.equals(Config.hypixelRank)) {
            Config.hypixelRank = newRank;
            Config.save();
        }
    }

    public static boolean isNon() {
        return "NONE".equals(Config.hypixelRank);
    }

    public static boolean canSupercraft() {
        return !isNon();
    }
}