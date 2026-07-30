package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import org.jetbrains.annotations.Nullable;

public enum DwarvenEvent {
    UNKNOWN("§7UNKNOWN§r", true),

    GONE_WITH_THE_WIND("§9GONE WITH THE WIND§r", true),
    GOBLIN_RAID("§cGOBLIN RAID§r", false),
    BETTER_TOGETHER("§dBETTER TOGETHER§r", true),
    RAFFLE("§6RAFFLE§r", false),
    MITHRIL_GOURMAND("§bMITHRIL GOURMAND§r", false),
    DOUBLE_POWDER("§b2X POWDER§r", true);

    private String displayName;
    private boolean isPassive;

    DwarvenEvent(String displayName, boolean isPassive) {
        this.displayName = displayName;
        this.isPassive = isPassive;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isPassive() {
        return isPassive;
    }

    @Nullable
    public static DwarvenEvent fromString(String event) {
        try {
            return DwarvenEvent.valueOf(event);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}