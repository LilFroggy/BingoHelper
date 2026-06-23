package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScreenUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static String getTitle() {
        return CLIENT.screen != null ? CLIENT.screen.getTitle().getString() : ""; 
    }

    public static NonNullList<Slot> getSlots() {
        if (!(CLIENT.player instanceof LocalPlayer player)) return NonNullList.create();
        if (!(player.containerMenu instanceof AbstractContainerMenu handler)) return NonNullList.create();
        return handler.slots;
    }

    public static ItemStack getCursorStack() {
        if (!(CLIENT.player instanceof LocalPlayer player)) return ItemStack.EMPTY;
        if (!(player.containerMenu instanceof AbstractContainerMenu handler)) return ItemStack.EMPTY;
        return handler.getCarried();
    }
}