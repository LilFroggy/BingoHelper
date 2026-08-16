package io.github.lilfroggy.bingohelper.util.dwarvenEvents;

import io.github.lilfroggy.bingohelper.util.ChatLib;

public class Event {
    transient public Type type;
    transient public String name;
    transient public String displayName;
    public long starts_at_max;
    public long ends_at_max;

    public boolean isDying() {
        return System.currentTimeMillis() > starts_at_max;
    }

    public long secondsTillLastStart() {
        return (starts_at_max - System.currentTimeMillis()) / 1000;
    }

    public long secondsTillLastEnd() {
        return (ends_at_max - System.currentTimeMillis()) / 1000;
    }

    public String timeTillLastStart() {
        return "§a" + ChatLib.formatSeconds(secondsTillLastStart());
    }

    public String timeTillLastEnd() {
        return "§6" + ChatLib.formatSeconds(secondsTillLastEnd());
    }

    public String time() {
        return isDying() ? timeTillLastEnd() : timeTillLastStart();
    }

    public String displayString() {
        return type.isPassive() ? displayName + " §r§7(" + time() + "§r§7)" : displayName;
    }

    public String toString() {
        return "{" +
                "\n  type: " + type.toString() +
                "\n  name: " + name +
                "\n  displayName: " + displayName +
                "\n  starts_in_max: " + secondsTillLastStart() +
                "\n  ends_in_max: " + secondsTillLastEnd() +
                "\n}";
    }
}