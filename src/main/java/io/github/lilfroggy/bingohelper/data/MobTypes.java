package io.github.lilfroggy.bingohelper.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.Client;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.mixin.accessor.AbstractContainerScreenAccessor;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.util.render.RenderLib;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;

public class MobTypes {
    public static enum MobType {
        UNKNOWN("?", "Unknown"),
        AIRBORNE("\uE070", "Airborne"),
        ANIMAL("\uE071", "Animal"),
        AQUATIC("\uE072", "Aquatic"),
        ARCANE("\uE073", "Arcane"),
        ARTHROPOD("\uE074", "Arthropod"),
        CONSTRUCT("\uE075", "Construct"),
        CUBIC("\uE076", "Cubic"),
        ELUSIVE("\uE077", "Elusive"),
        ENDER("\uE078", "Ender"),
        FROZEN("\uE079", "Frozen"),
        GLACIAL("\uE07A", "Glacial"),
        HUMANOID("\uE07B", "Humanoid"),
        INFERNAL("\uE07C", "Infernal"),
        MAGMATIC("\uE07D", "Magmatic"),
        MYTHOLOGICAL("\uE07E", "Mythological"),
        PEST("\uE018", "Pest"),
        SHIELDED("\uE080", "Shielded"),
        SKELETAL("\uE081", "Skeletal"),
        SPOOKY("\uE082", "Spooky"),
        SUBTERRANEAN("\uE083", "Subterranean"),
        UNDEAD("\uE084", "Undead"),
        WITHER("\uE085", "Wither"),
        WOODLAND("\uE086", "Woodland");

        private static final Map<String, MobType> BY_SYMBOL = new HashMap<>();
        private static final Map<String, MobType> BY_NAME = new HashMap<>();
        private static final Map<String, MobType> BY_DISPLAY_NAME = new HashMap<>();
        private static final Pattern PATTERN = Pattern.compile(
            "(?<symbol>[" + Arrays.stream(values())
                    .filter(type -> type != UNKNOWN)
                    .map(type -> Pattern.quote(type.symbol))
                    .collect(Collectors.joining()) + "]) (?<name>[A-Za-z]+)"
        );

        final String symbol;
        final String name;
        final String displayName;

        MobType(String symbol, String name) {
            this.symbol = symbol;
            this.name = name;
            this.displayName = symbol + " " + name;
        }

        static {
            for (MobType type : values()) {
                if (type == UNKNOWN) continue;
                BY_SYMBOL.put(type.symbol, type);
                BY_NAME.put(type.name, type);
                BY_DISPLAY_NAME.put(type.displayName, type);
            }
        }

        public static MobType bySymbol(String symbol) {
            return BY_SYMBOL.getOrDefault(symbol, UNKNOWN);
        }

        public static MobType byName(String name) {
            return BY_NAME.getOrDefault(name, UNKNOWN);
        }

        public static MobType byDisplayName(String displayName) {
            return BY_DISPLAY_NAME.getOrDefault(displayName, UNKNOWN);
        }

        public static Pattern getPattern() {
            return PATTERN;
        }

        public String getSymbol() {
            return symbol;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    public static final Set<String> unlocked = new HashSet<>();

    public static void init() {
        Events.MESSAGE.register(MobTypes::onGameMessage);
        //Events.RENDER_SCREEN.register(MobTypes::onRenderScreen);
    }

    public static boolean hasUnlocked(String type) {
        return unlocked.contains(type);
    }

    public static boolean hasUnlocked(List<String> types) {
        return unlocked.containsAll(types);
    }

    public static Set<String> getUnlocked() {
        return new HashSet<>(unlocked);
    }

    public static void reset() {
        unlocked.clear();
    }

    public static void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!Skyblock.inBingo()) return;
        if (!unformattedMsg.startsWith("   ")) return;

        Matcher matcher = MobType.getPattern().matcher(unformattedMsg);

        boolean foundAny = false;
        while (matcher.find()) {
            String foundSymbol = matcher.group("symbol");
            String foundName = matcher.group("name");
            MobType prospectType = MobType.bySymbol(foundSymbol);
            String prospectName = prospectType.getName();
            if (prospectType == MobType.UNKNOWN || !prospectName.equals(foundName)) continue;

            foundAny = true;
            String id = ChatLib.toSnakeCase(prospectName);
            unlocked.add(id);
            Logger.info("Unlocked Mob Type: " + id, !Config.debug);
        }

        if (!foundAny) return;

        Events.UNLOCK_MOB_TYPE.invoke(listener -> listener.onUnlockMobType());
    }

    public static void onRenderScreen(GuiGraphicsExtractor graphics, Screen screen, String title, NonNullList<Slot> slots) {
        if (!title.equals("Collected Mob Types")) return;
        
        for (Slot slot : slots) {
            String itemName = slot.getItem().getHoverName().getString();
            Matcher matcher = MobType.getPattern().matcher(itemName);
            
            if (!matcher.matches()) continue;
            
            MobType mobType = MobType.bySymbol(matcher.group("symbol"));
            String displayName = mobType.getDisplayName();

            if (!itemName.equals(displayName)) continue;

            RenderLib.highlightSlot(graphics, slot, 0xFFFF78A5);
            Slot hoveredSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
            if (slot.equals(hoveredSlot)) graphics.text(Client.MINECRAFT.font, displayName, 0, -12, 0xFFFF78A5);
        }
    }
}