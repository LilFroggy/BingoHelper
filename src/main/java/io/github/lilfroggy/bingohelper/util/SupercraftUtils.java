package io.github.lilfroggy.bingohelper.util;

import org.jetbrains.annotations.Nullable;

import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SupercraftUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static enum CraftState {
        READY, CHECK_NEXT_PAGE, MISSING_INGREDIENTS, NO_INVENTORY_SPACE
    }

    private static final int OUTPUT_ITEM_INDEX = 25;
    private static final int SUPERCRAFT_BUTTON_INDEX = 32;
    private static final int NEXT_PAGE_INDEX = 53;

    public static void highlightSlot(GuiGraphicsExtractor graphics, NonNullList<Slot> slots, String desiredId) {
        if (!isCorrectScreen(slots, desiredId)) return;
        Slot target = SupercraftUtils.getHighlightSlot(slots);
        if (target == null || !target.hasItem()) return;
        RenderLib.highlightSlot(graphics, target, RenderLib.MINECRAFT_GREEN);
    }

    public static boolean craftedDesiredItem(NonNullList<Slot> slots, String desiredId, int clickedIndex) {
        if (isCorrectScreen(slots, desiredId)) return false;
        if (clickedIndex != SUPERCRAFT_BUTTON_INDEX) return false;
        if (getCraftState(slots) != CraftState.READY) return false;
        return true;
    }

    @Nullable
    public static Slot getHighlightSlot(NonNullList<Slot> slots) {
        return switch (getCraftState(slots)) {
            case CHECK_NEXT_PAGE -> slots.get(NEXT_PAGE_INDEX);
            case READY -> slots.get(SUPERCRAFT_BUTTON_INDEX);
            default -> null;
        };
    }

    public static CraftState getCraftState(NonNullList<Slot> slots) {
        String lore = Skyblock.getLore(slots.get(SUPERCRAFT_BUTTON_INDEX).getItem());
        if (lore.contains("Missing ingredients!")) {
            if (isNextPage(slots)) return CraftState.CHECK_NEXT_PAGE;
            else return CraftState.MISSING_INGREDIENTS;
        }
        if (lore.contains("No inventory space!")) return CraftState.NO_INVENTORY_SPACE;
        return CraftState.READY;
    }

    public static boolean isNextPage(NonNullList<Slot> slots) {
        return slots.get(NEXT_PAGE_INDEX).getItem().getHoverName().getString().equals("Next Recipe");
    }
    
    public static boolean isCorrectScreen(NonNullList<Slot> slots, String desiredId) {
        if (!(CLIENT.screen instanceof ContainerScreen screen)) return false;
        if (slots.size() < NEXT_PAGE_INDEX) return false;
        ItemStack outputItem = slots.get(OUTPUT_ITEM_INDEX).getItem();
        String outputId = Skyblock.getID(outputItem);
        if (!outputId.equals(desiredId)) return false;
        String outputName = outputItem.getHoverName().getString();
        return screen.getTitle().getString().equals(outputName);
    }
}