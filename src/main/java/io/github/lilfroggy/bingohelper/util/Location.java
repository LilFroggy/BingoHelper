package io.github.lilfroggy.bingohelper.util;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.AreaChangeEventBus;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class Location {

    public static String serverType = null;
    public static boolean isInSkyblock = false;
    
    public static String AREA = null;
    public static String SUB_AREA = null;

    public static void onLocationPacket(ClientboundLocationPacket packet) {
        if (Config.debug) Logger.info("packet received: " + packet.toString());

        serverType = packet.getServerType().get().getName();
        isInSkyblock = serverType.equals("SkyBlock");

        String oldArea = AREA;
        AREA = packet.getMap().orElse(null);
        if (Config.debug) Logger.info("New location: " + AREA);

        if ((oldArea == null && AREA != null) || (oldArea != null && !oldArea.equals(AREA))) {
            AreaChangeEventBus.fire(AREA, oldArea);
        }

    }

}