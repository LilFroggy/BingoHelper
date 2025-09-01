package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public class GuiItemStep extends Step implements
        ClientTickEventBus.ClientTickListener {

    public String guiName;
    public int itemIndex;
    public String has;
    public String doesntHave;

    @Override
    public String additionalInstructionFormatting() {
        return instruction;
    }

    @Override
    public void onReset() {
        // Do nothing
    }

    @Override
    protected void onActivate() {
        ClientTickEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ClientTickEventBus.unregister(this);
    }

    @Override
    public void onClientTick(int tick) {

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // Check if we're in the correct GUI
        if (mc.currentScreen == null || !(mc.currentScreen instanceof HandledScreen)) return;
        
        // Check if we have a container open
        ScreenHandler screenHandler = mc.player.currentScreenHandler;
        if (screenHandler == null || screenHandler instanceof PlayerScreenHandler) return;
        
        // Check if the container name matches
        if (mc.currentScreen.getTitle() == null || 
            !mc.currentScreen.getTitle().getString().contains(guiName)) return;
        
        // If no criteria specified, advance to next step
        if ((has == null || has.isEmpty()) && (doesntHave == null || doesntHave.isEmpty())) {
            Guide.advance();
            return;
        }
        
        // Get the item at the specified index
        DefaultedList<ItemStack> containerItems = screenHandler.getStacks();
        if (itemIndex >= containerItems.size()) return;
        
        ItemStack item = containerItems.get(itemIndex);
        if (item == null || item.isEmpty()) return;
        
        // Get the lore and check conditions
        String lore = Skyblock.getLore(item);
        boolean done = true;
        
        // Check if item has required text
        if (has != null && !has.isEmpty()) {
            boolean hasInLore = lore.contains(has);
            boolean hasInName = item.getCustomName() != null && item.getCustomName().getString().contains(has);
            if (!hasInLore && !hasInName) {
                done = false;
            }
        }
        
        // Check if item doesn't have forbidden text
        if (doesntHave != null && !doesntHave.isEmpty()) {
            boolean hasInLore = lore.contains(doesntHave);
            boolean hasInName = item.getCustomName() != null && item.getCustomName().getString().contains(doesntHave);
            if (hasInLore || hasInName) {
                done = false;
            }
        }
        
        if (done) Guide.advance();
    }

}