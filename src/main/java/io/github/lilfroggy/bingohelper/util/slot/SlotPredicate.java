package io.github.lilfroggy.bingohelper.util.slot;

import io.github.lilfroggy.bingohelper.util.ColorUtils;
import io.github.lilfroggy.bingohelper.util.ItemUtils;
import io.github.lilfroggy.bingohelper.util.ScreenUtils;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotPredicate {
    private static final int DEFAULT_COLOR = RenderLib.MINECRAFT_GREEN;

    public String guiName;
    public Integer slotIndex;
    public String skyblockId;
    public List<String> has;
    public List<String> doesntHave;
    public Boolean playerInv;
    public String highlightColor;

    public SlotPredicate delegate;
    
    public transient Set<Slot> cache = new HashSet<>();;
    public int color;
    public int refCount;

    public SlotPredicate() {}

    public SlotPredicate(String guiName, Integer slotIndex, String skyblockId, List<String> has, List<String> doesntHave, Boolean playerInv, String highlightColor) {
        this.guiName = guiName;
        this.slotIndex = slotIndex;
        this.skyblockId = skyblockId;
        this.has = has;
        this.doesntHave = doesntHave;
        this.playerInv = playerInv;
        this.highlightColor = highlightColor;
    }

    public void init() {
        color = ColorUtils.hexToInt(highlightColor, DEFAULT_COLOR);
    }

    public void register() {
        if (delegate == null) {
            init();
            this.delegate = SlotRegistry.getOrCreate(this);
        }
    }

    public void unregister() {
        if (delegate != null) {
            SlotRegistry.release(delegate);
            delegate = null;
        }
    }

    public int highlightColor() {
        return color;
    }

    public SlotPredicate getDelegateOrSelf() {
        return delegate != null ? delegate : this;
    }

    public Set<Slot> getMatches() {
        return getDelegateOrSelf().cache;
    }

    public boolean hasMatch() {
        return !getDelegateOrSelf().cache.isEmpty();
    }

    public void scanInventory() {
        cache.clear();
        for (Slot slot : ScreenUtils.slots.ALL) {
            if (matches(slot)) {
                cache.add(slot);
            }
        }
    }

    public boolean matches(Slot slot) {
        return matchesName() && matchesIndex(slot) && matchesSkyblockId(slot) && has(slot) && doesntHave(slot) && isCorrectInventory(slot);
    }

    private boolean matchesName() {
        return guiName == null || ScreenUtils.title.startsWith(guiName);
    }

    private boolean matchesIndex(Slot slot) {
        return slotIndex == null || slot.getContainerSlot() == slotIndex;
    }

    private boolean matchesSkyblockId(Slot slot) {
        return skyblockId == null || skyblockId.equals(ItemUtils.getId(slot.getItem()));
    }

    private boolean has(Slot slot) {
        if (has == null) return true;
        ItemStack item = slot.getItem();
        String name = item.getHoverName().getString();
        String lore = ItemUtils.getLore(item);
        return has.stream().allMatch(line -> lore.contains(line) || name.contains(line));
    }

    private boolean doesntHave(Slot slot) {
        if (doesntHave == null) return true;
        String lore = ItemUtils.getLore(slot.getItem());
        return doesntHave.stream().allMatch(line -> !lore.contains(line));
    }

    private boolean isCorrectInventory(Slot slot) {
        if (playerInv == null) return !(slot.container instanceof Inventory);
        return playerInv == slot.container instanceof Inventory;
    }

    public void incrementRef() {
        refCount++;
    }

    public void decrementRef() {
        refCount--;
    
    }
    public int getRefCount() {
        return refCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotPredicate that = (SlotPredicate) o;
        return Objects.equals(guiName, that.guiName) &&
               Objects.equals(slotIndex, that.slotIndex) &&
               Objects.equals(skyblockId, that.skyblockId) &&
               Objects.equals(has, that.has) &&
               Objects.equals(doesntHave, that.doesntHave) &&
               Objects.equals(playerInv, that.playerInv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiName, slotIndex, skyblockId, has, doesntHave, playerInv);
    }
}