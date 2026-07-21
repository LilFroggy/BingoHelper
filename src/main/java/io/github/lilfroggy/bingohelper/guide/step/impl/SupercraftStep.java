package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.events.interfaces.RenderScreenEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.PlayerRank;
import io.github.lilfroggy.bingohelper.util.SupercraftUtils;
import io.github.lilfroggy.bingohelper.util.ScreenUtils.ScreenSlots;
import io.github.lilfroggy.bingohelper.util.item.HasList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class SupercraftStep extends Step implements ClientTickEndEvent, RenderScreenEvent {

    // Provided from step

    public HasList items;

    // internal

    private String desiredId;

    @Override
    public String formattedInstruction() {
        String formatted = instruction;
        int i = 1;

        for (var entry : items.entrySet()) {
            String placeholder = "%" + (i++) + "%";
            var info = entry.getValue();

            if (info.done()) formatted = formatted.replace(placeholder, "&a(✔)");
            else formatted = formatted.replace(placeholder, "(" + info.count() + "/" + info.target() + ")");
        }
        return formatted;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        items.reset();
    }

    @Override
    protected void onActivate() {
        setNextDesiredId();
        
        Events.CLIENT_TICK_END.register(this);
        Events.RENDER_SCREEN.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
        Events.RENDER_SCREEN.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (items.hasAll()) complete();
        else if (items.get(desiredId).done()) setNextDesiredId();
    }

    @Override
    public void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, ScreenSlots slots) {
        SupercraftUtils.highlightSlot(graphics, slots.CONTAINER, desiredId);
    }

    private void setNextDesiredId() {
        desiredId = items.anUnfinishedId();
        command = PlayerRank.canSupercraft() ? "/viewrecipe " + desiredId : "/craft";
    }
}