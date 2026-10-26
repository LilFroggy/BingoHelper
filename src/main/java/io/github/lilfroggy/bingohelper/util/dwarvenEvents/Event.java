package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import io.github.lilfroggy.bingohelper.util.ChatLib;

public class Event {
    transient public Type type;
    transient public String name;
    transient public String displayName;
    public long starts_at_max;
    public long ends_at_max;
    public boolean participated;

    public long secondsTillLastEnd() {
        return (ends_at_max - System.currentTimeMillis()) / 1000;
    }

    public String time() {
        return "§6" + ChatLib.formatSecondsShort(secondsTillLastEnd());
    }

    public String displayString() {
        return type.isPassive() ? displayName + " §r§7(" + time() + "§r§7)" : participated ? "§7" + ChatLib.removeFormatting(displayName) + "§r" : displayName;
    }

    public String toString() {
        return "{" +
                "\n  type: " + type.toString() +
                "\n  name: " + name +
                "\n  displayName: " + displayName +
                "\n  ends_in_max: " + secondsTillLastEnd() +
                "\n}";
    }
}