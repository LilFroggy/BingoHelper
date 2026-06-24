package io.github.lilfroggy.bingohelper.events.interfaces;

import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface ClickSlotEvent {
    void onClickSlot(Slot slot, int slotId, int button, ContainerInput actionType, CallbackInfo ci);
}