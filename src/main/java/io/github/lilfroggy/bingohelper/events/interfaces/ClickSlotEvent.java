package io.github.lilfroggy.bingohelper.events.interfaces;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public interface ClickSlotEvent {
    void onClickSlot(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci);
}