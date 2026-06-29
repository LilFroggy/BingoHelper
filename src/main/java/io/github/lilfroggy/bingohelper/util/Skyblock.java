package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import net.hypixel.data.region.Environment;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPlayerInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

public class Skyblock {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private static final String BINGO_SYMBOL = "Ⓑ";
    private static final String IRONMAN_SYMBOL = "♲";
    private static final String BINGO_TAB_REGEX = "^Profile: (.+) " + BINGO_SYMBOL + "$";
    private static final String IRONMAN_TAB_REGEX = "^Profile: (.+) " + IRONMAN_SYMBOL + "$";

    private static String[] symbols = {BINGO_SYMBOL, IRONMAN_SYMBOL};
    private static String[] regexes = {BINGO_TAB_REGEX, IRONMAN_TAB_REGEX};

    private static boolean inSkyblock = false;
    private static boolean inBingo = false;

    private static String area = null;
    private static String subArea = null;

    public static void init() {
        Events.SCOREBOARD_UPDATE.register(Skyblock::onScoreboardUpdate);
        Events.CLIENT_TICK_END.register(Skyblock::onClientTickEnd);
    }

    public static void onHelloPacket(ClientboundHelloPacket packet) {
        if (Config.debug) Logger.info("packet received: " + packet.toString());
        var isAlpha = packet.getEnvironment() != Environment.PRODUCTION;
        Events.JOIN_HYPIXEL.invoke(listener -> listener.onJoinHypixel(isAlpha));
    }

    public static void onLocationPacket(ClientboundLocationPacket packet) {
        HypixelModAPI.getInstance().sendPacket(new ServerboundPlayerInfoPacket());

        if (Config.debug) Logger.info("packet received: " + packet.toString());

        inSkyblock = packet.getServerType().get().getName().equals("SkyBlock");

        String oldArea = area;
        area = packet.getMap().orElse(null);
        if (Config.debug) Logger.info("New location: " + area);

        if ((oldArea == null && area != null) || (oldArea != null && !oldArea.equals(area))) {
            Events.CHANGE_AREA.invoke(listener -> listener.onAreaChange(area, oldArea));
        }
    }

    public static void onScoreboardUpdate(ArrayList<String> lines) {
        String oldSubArea = subArea;
        String newSubArea = null;
        for (String line : lines) {
            if (!line.contains("⏣") && !line.contains("ф")) continue;
            newSubArea = line.replaceAll("[⏣ф]", "").strip();
            break;
        }
        subArea = newSubArea;
        if ((oldSubArea == null && newSubArea != null) || (oldSubArea != null && !oldSubArea.equals(newSubArea))) {
            Events.CHANGE_SUB_AREA.invoke(listener -> listener.onSubAreaChange(subArea, oldSubArea));
        }
    }

    public static void onClientTickEnd(int tick) {
        if (tick % 20 != 0) return;
        if (CLIENT.player == null || CLIENT.level == null) return;

        boolean wasBingo = inBingo;

        if (alwaysBingo()) inBingo = true;
        else if (bingoInName()) inBingo = true;
        else if (bingoInTab()) inBingo = true;
        else inBingo = false;

        if (wasBingo == inBingo) return;

        if (inBingo) Events.JOIN_BINGO.invoke(listener -> listener.onJoinBingo());
        else Events.LEAVE_BINGO.invoke(listener -> listener.onLeaveBingo());
        
        if (Config.debug) Logger.info("In Bingo: " + (inBingo ? "§a" : "§c") + inBingo);
    }

    private static boolean alwaysBingo() {
        return Config.gamemodeIndex == 2;
    }

    private static boolean bingoInName() {
        if (!(CLIENT.player instanceof LocalPlayer player)) return false;
        return player.getDisplayName().getString().contains(symbols[Config.gamemodeIndex]);
    }

    private static boolean bingoInTab() {
        return Tablist.getLines().stream().anyMatch(line -> line.matches(regexes[Config.gamemodeIndex]));
    }

    public static boolean inSkyblock() {
        return inSkyblock;
    }

    public static boolean inBingo() {
        return inBingo;
    }

    public static String area() {
        return area;
    }

    public static String subArea() {
        return subArea;
    }
























    /**
     * Extracts the Skyblock item ID from an ItemStack's NBT data.
     * For enchanted books, returns "ENCHANTMENT_NAME_LEVEL" format.
     *
     * @param item The ItemStack to extract ID from
     * @return The Skyblock item ID, or an empty string if not found/invalid
     */
    public static String getID(ItemStack item) {
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

    /**
     * Extracts the Skyblock item reforge from an ItemStack's NBT data.
     *
     * @param item The ItemStack to extract ID from
     * @return The Skyblock item ID, or null if not found/invalid
     */
    @Nullable
    public static String getReforge(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        CompoundTag nbt = getNbt(item);
    
        if (nbt == null) return null;

        return ChatLib.toTitleCase(nbt.getString("modifier").orElse(null));
    }

    /**
     * Extracts Skyblock item lore from an ItemStack's NBT data.
     *
     * @param item The ItemStack to extract lore from
     * @return The Skyblock item lore as a single string, or null if not found/invalid
     */
    public static String getLore(ItemStack item) {
        if (item == null || item.isEmpty()) return "";

        ItemLore loreComponent = item.get(DataComponents.LORE);
        if (loreComponent == null) return "";

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
        int count = 0;

        if (!(CLIENT.player instanceof LocalPlayer player)) return 0;
        if (!(player.containerMenu instanceof AbstractContainerMenu handler)) return 0;

        var slots = handler.slots;

        for (Slot slot : slots) {
            if (!(slot.container instanceof Inventory)) continue;
            ItemStack stack = slot.getItem();
            if (stack == null || stack.isEmpty()) continue;
            String itemId = getID(stack);
            if (id.equals(itemId)) {
                count += stack.getCount();
            }
        }

        return count;
    }
}