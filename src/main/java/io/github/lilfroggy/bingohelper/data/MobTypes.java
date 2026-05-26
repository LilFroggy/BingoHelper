package io.github.lilfroggy.bingohelper.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;

public class MobTypes {
    private static final List<String> IDS = List.of(
        "✈ Airborne",
        "☮ Animal",
        "⚓ Aquatic",
        "♃ Arcane",
        "Ж Arthropod",
        "⚙ Construct",
        "⚂ Cubic",
        "♣ Elusive",
        "⊙ Ender",
        "☃ Frozen",
        "❄ Glacial",
        "✰ Humanoid",
        "♨ Infernal",
        "♆ Magmatic",
        "✿ Mythological",
        "ൠ Pest",
        "⛨ Shielded",
        "🦴 Skeletal",
        "☽ Spooky",
        "⛏ Subterranean",
        "༕ Undead",
        "☠ Wither",
        "⸙ Woodland"
    );

    // ✈ Airborne, ☮ Animal, ⚓ Aquatic, ♃ Arcane, Ж Arthropod, ⚙ Construct, ⚂ Cubic, ♣ Elusive, ⊙ Ender, ☃ Frozen, ❄ Glacial, ✰ Humanoid, ♨ Infernal, ♆ Magmatic, ✿ Mythological, ൠ Pest, ⛨ Shielded, 🦴 Skeletal, ☽ Spooky, ⛏ Subterranean, ༕ Undead, ☠ Wither, ⸙ Woodland
    private static final Pattern pattern = Pattern.compile("(?:["+IDS.stream().map(id -> Pattern.quote(id.split(" ")[0])).collect(Collectors.joining())+"]) ([A-Za-z]+)");

    public static final Set<String> mobTypes = new HashSet<>();

    public static void init() {
        Events.MESSAGE.register(MobTypes::onGameMessage);
    }

    public static boolean hasUnlocked(String type) {
        return mobTypes.contains(type);
    }

    public static boolean hasUnlocked(List<String> types) {
        return mobTypes.containsAll(types);
    }

    public static Set<String> getUnlocked() {
        return new HashSet<>(mobTypes);
    }

    public static void reset() {
        mobTypes.clear();
    }

    public static void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!Skyblock.inBingo()) return;
        if (!unformattedMsg.startsWith("   ")) return;

        Matcher matcher = pattern.matcher(unformattedMsg);

        boolean foundAny = false;
        while (matcher.find()) {
            foundAny = true;
            String unlocked = ChatLib.toSnakeCase(matcher.group(1));
            mobTypes.add(unlocked);
            Logger.info("Unlocked Mob Type: " + unlocked, !Config.debug);
        }

        if (!foundAny) return;

        Events.UNLOCK_MOB_TYPE.invoke(listener -> listener.onUnlockMobType());
    }
}