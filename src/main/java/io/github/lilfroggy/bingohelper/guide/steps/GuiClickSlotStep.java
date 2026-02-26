package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.MouseClickEventBus;
import io.github.lilfroggy.bingohelper.events.ScreenRenderEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class GuiClickSlotStep extends Step implements
        MouseClickEventBus.MouseClickListener,
        ScreenRenderEventBus.ScreenRenderListener {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public String guiName;
    public int slotIndex;

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
        MouseClickEventBus.register(this);
        ScreenRenderEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        MouseClickEventBus.unregister(this);
        ScreenRenderEventBus.unregister(this);
    }

    @Override
    public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (CLIENT.player == null) return;

        // Check if we're in the correct GUI
        if (CLIENT.currentScreen == null || !(CLIENT.currentScreen instanceof HandledScreen)) return;
        
        // Check if we have a container open
        ScreenHandler screenHandler = CLIENT.player.currentScreenHandler;
        if (screenHandler == null || screenHandler instanceof PlayerScreenHandler) return;
        
        // Check if the container name matches
        if (CLIENT.currentScreen.getTitle() == null || !CLIENT.currentScreen.getTitle().getString().contains(guiName)) return;

        if (slotId != slotIndex) return;

        Guide.advance();
    }

    @Override
    public void onScreenRender(DrawContext context, Screen screen, String title, DefaultedList<Slot> slots) {
        if (!title.contains(guiName)) return;
        RenderLib.highlightSlot(context, slots.get(slotIndex), RenderLib.MINECRAFT_GREEN);
    }

}