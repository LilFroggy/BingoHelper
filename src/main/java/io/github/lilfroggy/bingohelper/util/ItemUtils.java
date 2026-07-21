package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import io.github.lilfroggy.bingohelper.Client;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

public class ItemUtils {

    public static String getId(ItemStack item) {
        if (item == null || item.isEmpty()) return "";

        CompoundTag nbt = getNbt(item);
        if (nbt == null) return "";

        return nbt.getString("id").orElse("");
    }

    @Nullable
    public static CompoundTag getNbt(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        CustomData nbtComponent = item.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null || nbtComponent.isEmpty()) return null;

        CompoundTag nbt = nbtComponent.copyTag();
        if (nbt == null || nbt.isEmpty()) return null;
        return nbt;
    }

    @Nullable
    public static List<String> getEnchants(ItemStack item) {
        CompoundTag nbt = getNbt(item);
    
        if (nbt == null) return null;

        CompoundTag enchants = nbt.getCompound("enchantments").orElse(null);

        if (enchants == null) return null;

        List<String> enchantmentList = new ArrayList<>();

        for (String key : enchants.keySet()) {
            int level = enchants.getIntOr(key, 0);
            enchantmentList.add(key.toUpperCase() + "_" + level);
        }
        
        return enchantmentList;
    }

    @Nullable
    public static String getReforge(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        CompoundTag nbt = getNbt(item);
    
        if (nbt == null) return null;

        return ChatLib.toTitleCase(nbt.getString("modifier").orElse(null));
    }

    public static String getLore(ItemStack item) {
        if (item == null || item.isEmpty()) return "";

        ItemLore loreComponent = item.get(DataComponents.LORE);
        if (loreComponent == null) return "";

        return loreComponent.lines().stream()
                .map(text -> text.getString())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    public static int getCount(String id) {
        if (id == null) return 0;
        int count = 0;

        if (!(Client.MINECRAFT.player instanceof LocalPlayer player)) return 0;
        if (!(player.containerMenu instanceof AbstractContainerMenu handler)) return 0;

        var slots = handler.slots;

        for (Slot slot : slots) {
            if (!(slot.container instanceof Inventory)) continue;
            ItemStack stack = slot.getItem();
            if (stack == null || stack.isEmpty()) continue;
            String itemId = getId(stack);
            if (id.equals(itemId)) {
                count += stack.getCount();
            }
        }

        return count;
    }
}