package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Type {
    UNKNOWN("§7UNKNOWN§r"),

    GONE_WITH_THE_WIND("§9GONE WITH THE WIND§r"),
    BETTER_TOGETHER("§dBETTER TOGETHER§r"),
    DOUBLE_POWDER("§b2X POWDER§r"),
    GOBLIN_RAID("§cGOBLIN RAID§r", false, new String[] {
        "WOW! All",
        "OOF! Players"
    }),
    RAFFLE("§6RAFFLE§r", false, new String[] {
        "COOL! You personally collected"
    }),
    MITHRIL_GOURMAND("§bMITHRIL GOURMAND§r", false, new String[] {
        "BONUS! You gave Don Expresso"
    });

    private static final Map<String, Type> LOOKUP = new HashMap<>();
    private static final List<Type> PASSIVE = new ArrayList<>();
    private static final List<Type> ACTIVE = new ArrayList<>();

    static {
        for (Type type : values()) {
            if (type == UNKNOWN) continue;

            LOOKUP.put(type.name(), type);

            if (type.isPassive()) {
                PASSIVE.add(type);
            } else {
                ACTIVE.add(type);
            }
        }
    }

    private String displayName;
    private boolean isPassive;
    private String[] participationMessages;

    Type(String displayName) {
        this(displayName, true, new String[0]);
    }

    Type(String displayName, boolean isPassive, String[] participationMessages) {
        this.displayName = displayName;
        this.isPassive = isPassive;
        this.participationMessages = participationMessages;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isPassive() {
        return isPassive;
    }

    public String[] participationMessages() {
        return participationMessages;
    }

    public static Type of(String id) {
        if (id == null) return UNKNOWN;
        return LOOKUP.getOrDefault(id, UNKNOWN);
    }

    public static List<Type> passive() {
        return PASSIVE;
    }

    public static List<Type> active() {
        return ACTIVE;
    }
}