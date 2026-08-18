package io.github.lilfroggy.bingohelper.guide.step.properties.navTo;

import java.util.List;

import io.github.lilfroggy.bingohelper.command.CommandHandler;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.IslandChangeEvent;
import io.github.lilfroggy.bingohelper.guide.step.properties.outlineEntities.OutlineEntitiesProperty;
import io.github.lilfroggy.bingohelper.util.ChatLib;

public class NavToProperty implements ClientTickEndEvent, IslandChangeEvent {
    private static final String START_NAV_COMMAND = "shnav";
    private static final String STOP_NAV_COMMAND = "shstopnavigation";

    public List<OutlineEntitiesProperty> outlineEntities;
    public String navTo;

    private boolean isNavigating = false;
    private int cooldown = 0;

    public void register(List<OutlineEntitiesProperty> outlineEntities) {
        this.outlineEntities = outlineEntities;

        if (!commandsExist()) {
            ChatLib.chat("§cInstall SkyHanni to enable navigation!");
            return;
        }

        Events.CLIENT_TICK_END.register(this);
        Events.CHANGE_ISLAND.register(this);
    }

    public void unregister() {
        if (!commandsExist()) return;

        Events.CLIENT_TICK_END.unregister(this);
        Events.CHANGE_ISLAND.unregister(this);
        stopNav();
    }

    private boolean outlineEntityExists() {
        if (outlineEntities == null) return false;
        return outlineEntities.stream().anyMatch(e -> e.hasMatch());
    }

    public void updateNav() {
        if (!isNavigating && !outlineEntityExists()) {
            startNav();
        }
        else if (isNavigating && outlineEntityExists()) {
            stopNav();
        }
    }

    public void startNav() {
        ChatLib.command(START_NAV_COMMAND + " " + navTo);
        isNavigating = true;
    }

    public void stopNav() {
        ChatLib.command(STOP_NAV_COMMAND);
        isNavigating = false;
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        updateNav();
    }

    @Override
    public void onIslandChange(String oldIsland, String newIsland) {
        stopNav();
        cooldown = 5; // SkyHanni checks every meaningful tick so waiting 5 should be safe
    }

    public boolean commandsExist() {
        return CommandHandler.exists(START_NAV_COMMAND) && CommandHandler.exists(STOP_NAV_COMMAND);
    }

    @Override
    public String toString() {
        return "NavToProperty{" +
                "navTo='" + navTo + '\'' +
                ", isNavigating=" + isNavigating +
                ", cooldown=" + cooldown +
                ", commandsExist=" + commandsExist() +
                '}';
    }
}