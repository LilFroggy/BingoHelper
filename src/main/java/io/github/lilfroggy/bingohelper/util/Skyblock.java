package io.github.lilfroggy.bingohelper.util;

import java.util.ArrayList;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.AreaChangeEventBus;
import io.github.lilfroggy.bingohelper.events.ClientTickEventBus;
import io.github.lilfroggy.bingohelper.events.JoinBingoEventBus;
import io.github.lilfroggy.bingohelper.events.LeaveBingoEventBus;
import io.github.lilfroggy.bingohelper.events.ScoreboardUpdateEventBus;
import io.github.lilfroggy.bingohelper.events.SubAreaChangeEventBus;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;

public class Skyblock {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

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
        ScoreboardUpdateEventBus.register(Skyblock::onScoreboardUpdate);
        ClientTickEventBus.register(Skyblock::onClientTick);
    }

    public static void onLocationPacket(ClientboundLocationPacket packet) {
        if (Config.debug) Logger.info("packet received: " + packet.toString());

        inSkyblock = packet.getServerType().get().getName().equals("SkyBlock");

        String oldArea = area;
        area = packet.getMap().orElse(null);
        if (Config.debug) Logger.info("New location: " + area);

        if ((oldArea == null && area != null) || (oldArea != null && !oldArea.equals(area))) {
            AreaChangeEventBus.fire(area, oldArea);
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
            SubAreaChangeEventBus.fire(newSubArea, oldSubArea);
        }
    }

    public static void onClientTick(int tick) {
        if (tick % 20 != 0) return;
        if (CLIENT.player == null || CLIENT.world == null) return;

        boolean wasBingo = inBingo;

        if (alwaysBingo()) inBingo = true;
        else if (bingoInName()) inBingo = true;
        else if (bingoInTab()) inBingo = true;
        else inBingo = false;

        if (wasBingo == inBingo) return;

        if (inBingo) JoinBingoEventBus.fire();
        else LeaveBingoEventBus.fire();
        
        if (Config.debug) ChatLib.chat("In Bingo: " + (inBingo ? "§a" : "§c") + inBingo);
    }

    private static boolean alwaysBingo() {
        return Config.gamemodeIndex == 2;
    }

    private static boolean bingoInName() {
        return CLIENT.player.getDisplayName().getString().contains(symbols[Config.gamemodeIndex]);
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