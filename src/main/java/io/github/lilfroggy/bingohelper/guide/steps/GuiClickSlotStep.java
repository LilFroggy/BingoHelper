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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class GuiClickSlotStep extends Step implements
        MouseClickEventBus.MouseClickListener,
        ScreenRenderEventBus.ScreenRenderListener {

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

        if (slotId != slotIndex) return;

        Guide.advance();
    }

    @Override
    public void onScreenRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        
        if (screen.getTitle() == null || screen.getTitle().getString() == null || screen.getTitle().getString().isEmpty()) return;
        
        if (!screen.getTitle().getString().contains(guiName)) return;
        
        RenderLib.highlightContainerSlot(drawContext, slotIndex, RenderLib.MINECRAFT_GREEN);
    }

}