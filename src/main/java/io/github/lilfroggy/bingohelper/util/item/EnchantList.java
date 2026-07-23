package io.github.lilfroggy.bingohelper.util.item;

import java.util.Map;
import java.util.Set;

import io.github.lilfroggy.bingohelper.util.ItemUtils;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnchantList {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private Map<String, EnchantInfo> items;

    public EnchantList(Map<String, EnchantInfo> items) {
        this.items = items;
    }

    public EnchantInfo get(String id) {
        return items.get(id);
    }

    public boolean contains(String id) {
        return items.containsKey(id);
    }

    public Set<Map.Entry<String, EnchantInfo>> entrySet() {
        return items.entrySet();
    }

    public Iterable<EnchantInfo> values() {
        return items.values();
    }

    public void reset() {
        items.values().forEach(EnchantInfo::reset);
    }

    public boolean allDone() {
        return items.values().stream().allMatch(EnchantInfo::done);
    }

    public boolean allEnchanted() {
        var containerSlots = ScreenUtils.slots.CONTAINER;
        if (containerSlots.isEmpty()) checkPlayerSlots();
        else checkContainerSlots(containerSlots);
        return allDone();
    }

    private void checkPlayerSlots() {
        if (CLIENT.player == null) return;

        for (ItemStack item : CLIENT.player.getInventory()) {
            checkItem(item);
        }
    }

    private void checkContainerSlots(NonNullList<Slot> slots) {
        boolean inEnchantTable = ScreenUtils.getTitle().contains("Enchant Item");

        for (Slot slot : slots) {
            if (!inEnchantTable) continue;
            checkItem(slot.getItem());
        }
    }

    private void checkItem(ItemStack item) {
        if (item == null || item.isEmpty()) return;
        String id = ItemUtils.getId(item);
        if (id.isEmpty()) return;
        if (!items.containsKey(id)) return;

        EnchantInfo info = items.get(id);

        String lore = ItemUtils.getLore(item);
        boolean hasAllEnchants = info.requiredEnchants().stream().allMatch(enchant -> 
            lore.matches(".*\\b" + enchant.replace(" ", "\\s+") + "\\b.*"));
        info.done = hasAllEnchants;
    }
}