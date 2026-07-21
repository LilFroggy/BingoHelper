package io.github.lilfroggy.bingohelper.util;

import io.github.lilfroggy.bingohelper.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScreenUtils {
    private static final Minecraft CLIENT = Client.MINECRAFT;
    public static String title = "";
    public static final ScreenSlots slots = new ScreenSlots();

    public static class ScreenSlots {
        private final NonNullList<Slot> EMPTY = NonNullList.create();
        public NonNullList<Slot> ALL = EMPTY;
        public NonNullList<Slot> INVENTORY = NonNullList.create();
        public NonNullList<Slot> CONTAINER = NonNullList.create();
    }

    public static void update() {
        updateTitle();
        updateSlots();
    }

    public static void updateTitle() {
        title = CLIENT.screen != null ? CLIENT.screen.getTitle().getString() : ""; 
    }

    public static void updateSlots() {
        clearSlots();

        if (!(CLIENT.player instanceof LocalPlayer player)) return;
        if (!(player.containerMenu instanceof AbstractContainerMenu handler)) return;

        slots.ALL = handler.slots;

        for (Slot slot : slots.ALL) {
            if (slot.container instanceof Inventory) {
                slots.INVENTORY.add(slot);
            } else {
                slots.CONTAINER.add(slot);
            }
        }
    }

    public static void clear() {
        clearTitle();
        clearSlots();
    }

    public static void clearTitle() {
        title = "";
    }

    public static void clearSlots() {
        slots.ALL = slots.EMPTY;
        slots.INVENTORY.clear();
        slots.CONTAINER.clear();
    }

    public static String getTitle() {
        return title;
    }

    public static ItemStack getCursorStack() {
        if (!(CLIENT.player instanceof LocalPlayer player)) return ItemStack.EMPTY;
        if (!(player.containerMenu instanceof AbstractContainerMenu handler)) return ItemStack.EMPTY;
        return handler.getCarried();
    }
}