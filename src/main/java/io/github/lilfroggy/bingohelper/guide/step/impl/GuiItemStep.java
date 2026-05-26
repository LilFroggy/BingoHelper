package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.ClientTickEndEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.minecraft.item.ItemStack;

public class GuiItemStep extends Step implements ClientTickEndEvent {

    public String guiName;
    public int itemIndex;
    public String has;
    public String doesntHave;

    @Override
    public String formattedInstruction() {
        return instruction;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        // Do nothing
    }

    @Override
    protected void onActivate() {
        Events.CLIENT_TICK_END.register(this);
    }

    @Override
    protected void onDeactivate() {
        Events.CLIENT_TICK_END.unregister(this);
    }

    @Override
    public void onClientTickEnd(int tick) {
        if (!ScreenUtils.getTitle().equals(guiName)) return;

        if (has == null && doesntHave == null) {
            complete();
            return;
        }
        
        var slots = ScreenUtils.getSlots();

        if (itemIndex >= slots.size()) return;
        
        ItemStack item = slots.get(itemIndex).getStack();

        if (item.isEmpty()) return;
        
        String lore = Skyblock.getLore(item);

        boolean done = true;
        
        if (has != null) {
            boolean hasInLore = lore.contains(has);
            boolean hasInName = item.getName().getString().contains(has);
            if (!hasInLore && !hasInName) {
                done = false;
            }
        }
        
        if (doesntHave != null) {
            boolean hasInLore = lore.contains(doesntHave);
            boolean hasInName = item.getName().getString().contains(doesntHave);
            if (hasInLore || hasInName) {
                done = false;
            }
        }
        
        if (done) complete();
    }

}