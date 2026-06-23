package io.github.lilfroggy.bingohelper.mixin;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    @Inject(method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, ClickType actionType, CallbackInfo ci) {
        Events.CLICK_SLOT.invoke(listener -> listener.onClickSlot(slot, slotId, button, actionType, ci));
    }

    /*@Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlotHead(DrawContext context, Slot slot, CallbackInfo ci) {
        SlotRenderEventBus.fireBefore(context, slot);
    }*/

    @Inject(method = "renderSlot", at = @At(value = "TAIL"))
    public void onDrawSlotTail(GuiGraphics graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        Events.RENDER_SLOT.invoke(listener -> listener.onRenderSlot(graphics, slot));
    }

    @Inject(method = "renderSlots", at = @At("TAIL"))
    private void onDrawSlots(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (Events.RENDER_SCREEN.getListeners().isEmpty()) return;

        Events.RENDER_SCREEN.invoke(listener -> listener.onRenderScreen(graphics, CLIENT.screen, ScreenUtils.getTitle(), ScreenUtils.getSlots()));
    }
}