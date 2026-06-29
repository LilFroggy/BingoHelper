package io.github.lilfroggy.bingohelper.util.item;

import java.util.Map;
import java.util.Set;

import io.github.lilfroggy.bingohelper.util.Skyblock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public class ReforgeList {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private final Map<String, ReforgeInfo> items;

    public ReforgeList(Map<String, ReforgeInfo> items) {
        this.items = items;
    }

    public ReforgeInfo get(String id) {
        return items.get(id);
    }

    public boolean contains(String id) {
        return items.containsKey(id);
    }

    public Set<Map.Entry<String, ReforgeInfo>> entrySet() {
        return items.entrySet();
    }

    public Iterable<ReforgeInfo> values() {
        return items.values();
    }

    public void reset() {
        items.values().forEach(ReforgeInfo::reset);
    }

    public boolean allDone() {
        return items.values().stream().allMatch(ReforgeInfo::done);
    }

    public boolean allReforged() {
        if (!(CLIENT.player instanceof LocalPlayer player)) return false;
    
        for (ItemStack item : player.getInventory()) {
            if (item.isEmpty()) continue;
    
            String id = Skyblock.getID(item);
            if (!contains(id)) continue;
    
            String reforge = Skyblock.getReforge(item);
            ReforgeInfo info = items.get(id);

            if (info.isValidReforge(reforge)) {
                info.done = true;
            }
            else {
                info.done = false;
            }
        }
        return allDone();
    }
}