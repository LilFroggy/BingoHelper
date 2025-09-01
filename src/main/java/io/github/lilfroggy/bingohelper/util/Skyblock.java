package io.github.lilfroggy.bingohelper.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;

public class Skyblock {

    public static boolean isInBingo = false;

    /**
     * Extracts the Skyblock item ID from an ItemStack's NBT data.
     * For enchanted books, returns "ENCHANTMENT_NAME_LEVEL" format.
     *
     * @param item The ItemStack to extract ID from
     * @return The Skyblock item ID, or null if not found/invalid
     */
    public static String getID(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        NbtComponent nbtComponent = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null || nbtComponent.isEmpty()) return null;

        NbtCompound nbt = nbtComponent.copyNbt();

        return nbt.getString("id").orElse(null);
    }

    /**
     * Extracts the Skyblock item reforge from an ItemStack's NBT data.
     *
     * @param item The ItemStack to extract ID from
     * @return The Skyblock item ID, or null if not found/invalid
     */
    public static String getReforge(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        NbtComponent nbtComponent = item.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null || nbtComponent.isEmpty()) return null;

        NbtCompound nbt = nbtComponent.copyNbt();

        return nbt.getString("modifier").orElse(null);
    }

    /**
     * Extracts Skyblock item lore from an ItemStack's NBT data.
     *
     * @param item The ItemStack to extract lore from
     * @return The Skyblock item lore as a single string, or null if not found/invalid
     */
    public static String getLore(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        LoreComponent loreComponent = item.get(DataComponentTypes.LORE);
        if (loreComponent == null) return null;

        // Convert lore lines to a single string with spaces
        return loreComponent.lines().stream()
                .map(text -> text.getString())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    /**
     * Returns the total number of items in the player's inventory whose Skyblock ID matches the given id.
     * Uses the getID method for comparison. Returns 0 if no items are found.
     *
     * @param id The Skyblock item ID to search for
     * @return The total count of matching items in the player's inventory
     */
    public static int getItemCount(String id) {
        if (id == null) return 0;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return 0;
        int count = 0;

        for (ItemStack stack : mc.player.getInventory().getMainStacks()) {
            if (stack == null || stack.isEmpty()) continue;
            String itemId = getID(stack);
            if (id.equals(itemId)) {
                count += stack.getCount();
            }
        }
        return count;
    }
}