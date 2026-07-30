package io.github.lilfroggy.bingohelper.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import net.hypixel.data.region.Environment;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPlayerInfoPacket;
import net.minecraft.client.Minecraft;

public class Skyblock {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static final char AREA = '\uE067';
	public static final char RIFT_AREA = '\uE020';
    private static final String AREA_ICON_REGEX = String.format("[%s%s]", AREA, RIFT_AREA);

    private static final Pattern SB_LEVEL_REGEX = Pattern.compile("SB Level: \\[(?<level>\\d+)\\] (?<xp>\\d+).*");

    private static final String BINGO_SYMBOL = "Ⓑ";
    private static final String IRONMAN_SYMBOL = "♲";
    private static final String BINGO_TAB_REGEX = "^Profile: (.+) " + BINGO_SYMBOL + "$";
    private static final String IRONMAN_TAB_REGEX = "^Profile: (.+) " + IRONMAN_SYMBOL + "$";

    private static String[] symbols = {BINGO_SYMBOL, IRONMAN_SYMBOL};
    private static String[] regexes = {BINGO_TAB_REGEX, IRONMAN_TAB_REGEX};

    private static boolean inSkyblock = false;
    private static boolean inBingo = false;

    private static String area = null;
    private static String subArea = null;

    private static int level = 0;

    public static void init() {
        Events.SCOREBOARD_UPDATE.register(Skyblock::onScoreboardUpdate);
        Events.TABLIST_UPDATE.register(Skyblock::onTablistUpdate);
        Events.CLIENT_TICK_END.register(Skyblock::onClientTickEnd);
    }

    public static void reset() {
        level = 0;
    }

    public static void onHelloPacket(ClientboundHelloPacket packet) {
        Logger.debug("packet received: " + packet.toString());
        var isAlpha = packet.getEnvironment() != Environment.PRODUCTION;
        Events.JOIN_HYPIXEL.invoke(listener -> listener.onJoinHypixel(isAlpha));
    }

    public static void onLocationPacket(ClientboundLocationPacket packet) {
        HypixelModAPI.getInstance().sendPacket(new ServerboundPlayerInfoPacket());

        Logger.debug("packet received: " + packet.toString());

        ServerType serverType = packet.getServerType().orElse(null);
        if (serverType == null || serverType.getName() == null) inSkyblock = false;
        else inSkyblock = serverType.getName().equals("SkyBlock");

        String oldArea = area;
        area = packet.getMap().orElse(null);

        if ((oldArea == null && area != null) || (oldArea != null && !oldArea.equals(area))) {
            Events.CHANGE_AREA.invoke(listener -> listener.onAreaChange(area, oldArea));
            Logger.debug("New area: " + area);
        }
    }

    public static void onScoreboardUpdate(List<String> lines) {
        String oldSubArea = subArea;
        String newSubArea = null;
        for (String line : lines) {
            if (line.indexOf(AREA) == -1 && line.indexOf(RIFT_AREA) == -1) continue;
            newSubArea = line.replaceAll(AREA_ICON_REGEX, "").strip();
            break;
        }
        subArea = newSubArea;
        if ((oldSubArea == null && newSubArea != null) || (oldSubArea != null && !oldSubArea.equals(newSubArea))) {
            Events.CHANGE_SUB_AREA.invoke(listener -> listener.onSubAreaChange(subArea, oldSubArea));
            Logger.debug("New subArea: " + newSubArea);
        }
    }

    public static void onTablistUpdate(List<String> lines) {
        if (!inBingo) return;

        for (String line : lines) {
            Matcher matcher = SB_LEVEL_REGEX.matcher(line);
            if (!matcher.matches()) continue;
            String lvl = matcher.group("level");
            if (lvl == null) continue;
            int newLevel = Integer.valueOf(lvl).intValue();
            if (newLevel == level) continue;
            Logger.debug("Skyblock Level Changed: " + level + " -> " + newLevel);
            level = newLevel;
            Events.SKYBLOCK_LEVEL_CHANGE.invoke(listener -> listener.onSkyblockLevelChange(newLevel));
        }
    }

    public static void onClientTickEnd(int tick) {
        if (tick % 20 != 0) return;
        if (CLIENT.player == null || CLIENT.level == null) return;

        boolean wasBingo = inBingo;

        if (alwaysBingo()) inBingo = true;
        else if (bingoInName()) inBingo = true;
        else if (bingoInTab()) inBingo = true;
        else inBingo = false;

        if (wasBingo == inBingo) return;

        if (inBingo) Events.JOIN_BINGO.invoke(listener -> listener.onJoinBingo());
        else Events.LEAVE_BINGO.invoke(listener -> listener.onLeaveBingo());
        
        Logger.debug("In Bingo: " + (inBingo ? "§a" : "§c") + inBingo);
    }

    private static boolean alwaysBingo() {
        return Config.gamemodeIndex == 2;
    }

    private static boolean bingoInName() {
        String displayName = PlayerUtils.getDisplayName(CLIENT.player); 
        return displayName.contains(symbols[Config.gamemodeIndex]);
    }

    private static boolean bingoInTab() {
        return Tablist.getLines().stream().anyMatch(line -> line.matches(regexes[Config.gamemodeIndex]));
    }

    public static boolean inSkyblock() {
        return inSkyblock;
    }

    public static boolean inBingo() {
        return inBingo;
    }

    public static String area() {
        return area;
    }

    public static String subArea() {
        return subArea;
    }

    public static int level() {
        return level;
    }
}