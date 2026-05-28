package io.github.lilfroggy.bingohelper.mixin;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        Events.CLICK_SLOT.invoke(listener -> listener.onClickSlot(slot, slotId, button, actionType, ci));
    }

    /*@Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlotHead(DrawContext context, Slot slot, CallbackInfo ci) {
        SlotRenderEventBus.fireBefore(context, slot);
    }*/

    @Inject(method = "drawSlot", at = @At(value = "TAIL"))
    public void onDrawSlotTail(DrawContext context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        Events.RENDER_SLOT.invoke(listener -> listener.onRenderSlot(context, slot));
    }

    @Inject(method = "drawSlots", at = @At("TAIL"))
    private void onDrawSlots(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if (Events.RENDER_SCREEN.getListeners().isEmpty()) return;

        Events.RENDER_SCREEN.invoke(listener -> listener.onRenderScreen(context, CLIENT.currentScreen, ScreenUtils.getTitle(), ScreenUtils.getSlots()));
    }
}