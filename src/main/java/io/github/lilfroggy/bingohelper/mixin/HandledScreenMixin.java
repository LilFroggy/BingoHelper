package io.github.lilfroggy.bingohelper.mixin;

import io.github.lilfroggy.bingohelper.events.MouseClickEventBus;
import io.github.lilfroggy.bingohelper.events.ScreenRenderEventBus;
import io.github.lilfroggy.bingohelper.events.SlotRenderEventBus;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        MouseClickEventBus.onMouseClick(slot, slotId, button, actionType, ci);
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlotHead(DrawContext context, Slot slot, CallbackInfo ci) {
        SlotRenderEventBus.fireBefore(context, slot);
    }

    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void onDrawSlotTail(DrawContext context, Slot slot, CallbackInfo ci) {
        SlotRenderEventBus.fireAfter(context, slot);
    }

    @Inject(method = "drawSlots", at = @At("TAIL"))
    private void onDrawSlots(DrawContext context, CallbackInfo ci) {
        ScreenRenderEventBus.fire(context);
    }
}