package io.github.lilfroggy.bingohelper.events;

import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class MouseClickEventBus {

    public interface MouseClickListener {
        /**
         * Called when a mouse click occurs on a slot in a HandledScreen
         * @param slot The slot that was clicked
         * @param slotId The ID of the slot
         * @param button The mouse button that was clicked
         * @param actionType The type of slot action
         * @param ci The callback info for cancelling the click
         */
        void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci);
    }

    private static final EventBus<MouseClickListener> BUS = new EventBus<>();

    public static void register(MouseClickListener listener) {
        BUS.register(listener);
    }

    public static void unregister(MouseClickListener listener) {
        BUS.unregister(listener);
    }

    public static void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        for (MouseClickListener listener : BUS.getListeners()) {
            try {
                listener.onMouseClick(slot, slotId, button, actionType, ci);
            } catch (Exception e) {
                System.err.println("Error in mouse click listener: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}