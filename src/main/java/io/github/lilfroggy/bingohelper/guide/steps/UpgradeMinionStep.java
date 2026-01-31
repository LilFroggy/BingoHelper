package io.github.lilfroggy.bingohelper.guide.steps;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.events.MouseClickEventBus;
import io.github.lilfroggy.bingohelper.events.ScreenRenderEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class UpgradeMinionStep extends Step implements
        ClientTickEventBus.ClientTickListener,
        MouseClickEventBus.MouseClickListener,
        ScreenRenderEventBus.ScreenRenderListener {

    // Provided from step

    public String minionId; // COBBLESTONE_GENERATOR_1 -> COBBLESTONE
    public int fromLevel;
    public int toLevel;

    // internal

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final int SLOT_SUPERCRAFT = 32;
    private static final int SLOT_NEXT_PAGE = 53;
    private static final int SLOT_BACK_BUTTON = 48;

    private String desiredBaseId;
    private int desiredLevel;
    private String desiredId;
    private String finalId;
    private String desiredName;
    private boolean navigating = true;

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
        desiredBaseId = minionId + "_GENERATOR_"; // COBBLESTONE_GENERATOR_
        finalId = desiredBaseId + toLevel;
        setDesiredLevel(fromLevel + 1);
        ClientTickEventBus.register(this);
        MouseClickEventBus.register(this);
        ScreenRenderEventBus.register(this);
    }

    @Override
    protected void onDeactivate() {
        ClientTickEventBus.unregister(this);
        MouseClickEventBus.unregister(this);
        ScreenRenderEventBus.unregister(this);
    }

    @Override
    public void onClientTick(int tick) {
        if (Skyblock.getItemCount(finalId) > 0) Guide.advance();
    }

    @Override
    public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        Screen screen = CLIENT.currentScreen;
        if (!(screen instanceof GenericContainerScreen)) return;

        ItemStack item = slot.getStack();
        if (item.isEmpty()) return;

        if (isNavigationScreen(screen)) handleNavigationClick(item);
        else if (isUpgradeScreen(screen)) handleUpgradeClick(item);
    }

    private void handleNavigationClick(ItemStack stack) {
        String id = Skyblock.getID(stack);
        if (id == null || !id.equals(desiredId)) return;

        desiredName = stack.getName().getString();
        navigating = false;
    }

    private void handleUpgradeClick(ItemStack stack) {
        if (!stack.getName().getString().equals("Supercraft")) return;

        String lore = Skyblock.getLore(stack);
        if (lore == null || lore.contains("Missing ingredients!") || lore.contains("No inventory space!")) return;

        setDesiredLevel(desiredLevel + 1);
        navigating = true;
    }

    @Override
    public void onScreenRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if (desiredLevel > toLevel) return;
        if (CLIENT.player == null || CLIENT.player.currentScreenHandler == null) return;

        var items = CLIENT.player.currentScreenHandler.getStacks();

        if (isNavigationScreen(screen)) highlightNavigationSlot(drawContext, items);
        else if (isUpgradeScreen(screen)) highlightUpgradeSlot(drawContext, items);
    }

    private void highlightNavigationSlot(DrawContext drawContext, DefaultedList<ItemStack> items) {
        findSlotByItemId(items, desiredId).ifPresent(slot -> RenderLib.highlightContainerSlot(drawContext, slot, RenderLib.MINECRAFT_GREEN));
    }

    private void highlightUpgradeSlot(DrawContext drawContext, DefaultedList<ItemStack> items) {
        if (navigating) RenderLib.highlightContainerSlot(drawContext, SLOT_BACK_BUTTON, RenderLib.MINECRAFT_GREEN);
        else if (isMissingIngredients(items) && isNextPage(items)) RenderLib.highlightContainerSlot(drawContext, SLOT_NEXT_PAGE, RenderLib.MINECRAFT_GREEN);
        else if (isInventorySpace(items)) RenderLib.highlightContainerSlot(drawContext, SLOT_SUPERCRAFT, RenderLib.MINECRAFT_GREEN);
    }

    private boolean isNavigationScreen(Screen screen) {
        return screen.getTitle().getString().endsWith("Minion Recipes");
    }
    
    private boolean isUpgradeScreen(Screen screen) {
        return screen.getTitle().getString().equals(desiredName + " Recipe");
    }

    private boolean isMissingIngredients(DefaultedList<ItemStack> items) {
        if (items.size() <= SLOT_SUPERCRAFT) return false;
        return Skyblock.getLore(items.get(SLOT_SUPERCRAFT)).contains("Missing ingredients!");
    }

    private boolean isNextPage(DefaultedList<ItemStack> items) {
        if (items.size() <= SLOT_NEXT_PAGE) return false;
        return items.get(SLOT_NEXT_PAGE).getName().getString().equals("Next Recipe");
    }

    private boolean isInventorySpace(DefaultedList<ItemStack> items) {
        if (items.size() <= SLOT_SUPERCRAFT) return false;
        return !Skyblock.getLore(items.get(SLOT_SUPERCRAFT)).contains("No inventory space!");
    }

    private OptionalInt findSlotByItemId(DefaultedList<ItemStack> items, String id) {
        for (int i = 0; i < items.size() - 36; i++) {
            if (id.equals(Skyblock.getID(items.get(i)))) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    private void setDesiredLevel(int level) {
        desiredLevel = level;
        if (desiredLevel > toLevel) return;
        desiredId = desiredBaseId + desiredLevel;
        command = "/viewrecipe " + desiredId;
    }

}