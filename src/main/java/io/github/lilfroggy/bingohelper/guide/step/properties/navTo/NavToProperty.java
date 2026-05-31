package io.github.lilfroggy.bingohelper.guide.step.properties.navTo;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.IslandChangeEvent;
import io.github.lilfroggy.bingohelper.guide.ActiveSteps;
import io.github.lilfroggy.bingohelper.util.ChatLib;

public class NavToProperty implements ClientTickEndEvent, IslandChangeEvent {

    public String navTo;

    private boolean isNavigating;
    private int cooldown;

    public void register() {
        isNavigating = false;
        Events.CLIENT_TICK_END.register(this);
        Events.CHANGE_ISLAND.register(this);
    }

    public void unregister() {
        Events.CLIENT_TICK_END.unregister(this);
        Events.CHANGE_ISLAND.unregister(this);
        stopNav();
    }

    public void updateNav() {
        if (!isNavigating && !ActiveSteps.anyOutlineEntityExists()) {
            startNav();
        }
        else if (isNavigating && ActiveSteps.anyOutlineEntityExists()) {
            stopNav();
        }
    }

    public void startNav() {
        ChatLib.command("shnav " + navTo);
        isNavigating = true;
    }

    public void stopNav() {
        ChatLib.command("shstopnavigation");
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
}