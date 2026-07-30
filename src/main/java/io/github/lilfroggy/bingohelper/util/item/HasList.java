package io.github.lilfroggy.bingohelper.util.item;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.github.lilfroggy.bingohelper.util.ItemUtils;

public class HasList {
    private Map<String, HasInfo> items;

    public HasList(Map<String, HasInfo> items) {
        this.items = items;
    }

    public HasInfo get(String id) {
        return items.get(id);
    }

    public boolean contains(String id) {
        return items.containsKey(id);
    }

    public Set<Map.Entry<String, HasInfo>> entrySet() {
        return items.entrySet();
    }

    public Iterable<HasInfo> values() {
        return items.values();
    }

    public void reset() {
        items.values().forEach(HasInfo::reset);
    }

    public boolean allDone() {
        return items.values().stream().allMatch(HasInfo::done);
    }

    /**
     * Updates all items based on current inventory state.
     * Returns true if all items in the list are now complete.
     */
    public boolean update() {
        boolean updated = false;

        for (var item : entrySet()) {
            String id = item.getKey();
            HasInfo info = item.getValue();

            info.count = ItemUtils.getCount(id);

            if (info.done()) continue;

            if (info.hasEnough()) {
                info.done = true;
                updated = true;
            }
        }
        return updated;
    }

    @Nullable
    public String anUnfinishedId() {
        for (var item : entrySet()) {
            HasInfo info = item.getValue();
            if (info.done()) continue;
            return item.getKey();
        }
        return null;
    }
}