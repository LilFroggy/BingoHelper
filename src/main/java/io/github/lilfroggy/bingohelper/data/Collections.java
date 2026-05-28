package io.github.lilfroggy.bingohelper.data;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;
import io.github.lilfroggy.bingohelper.events.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Collections {
    private static final Pattern COLLECTION_UNLOCK_REGEX = Pattern.compile("^COLLECTION UNLOCKED (.+)$");
    private static final Pattern COLLECTION_TIER_ONE_REGEX = Pattern.compile("^COLLECTION LEVEL UP (.+) (.+)$");
    private static final Pattern COLLECTION_LEVEL_UP_REGEX = Pattern.compile("^COLLECTION LEVEL UP (.+) (.+)➜(.+)$");

    private static final Map<String, Integer> collections = new HashMap<>();

    public static void init() {
        Events.CREATE_BINGO_PROFILE.register(Collections::onCreateBingoProfile);
        Events.MESSAGE.register(Collections::onGameMessage);
    }

    public static void onCreateBingoProfile() {
        reset();
        if (Config.debug) Logger.info("Collections reset");
    }
    
    public static void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!Skyblock.inBingo()) return;

        String msg = unformattedMsg.trim();

        Matcher collectionUnlock = COLLECTION_UNLOCK_REGEX.matcher(msg);
        Matcher collectionTierOne = COLLECTION_TIER_ONE_REGEX.matcher(msg);
        Matcher collectionLevelUp = COLLECTION_LEVEL_UP_REGEX.matcher(msg);
        
        if (collectionUnlock.matches()) set(collectionUnlock.group(1), 0);
        else if (collectionLevelUp.matches()) set(collectionLevelUp.group(1), collectionLevelUp.group(3));
        else if (collectionTierOne.matches()) set(collectionTierOne.group(1), collectionTierOne.group(2));
    }
    
    private static void set(String collection, Object level) {
        String id = ChatLib.toSnakeCase(collection);
        Integer newLevel = ChatLib.decodeNumeral(level.toString());
        Integer oldLevel = getLevel(id);
        collections.put(id, newLevel);

        if (Config.debug) Logger.info("Collection updated: " + id + " " + oldLevel + " -> " + newLevel);
        
        Events.LEVEL_COLLECTION.invoke(listener -> listener.onLevelCollection(id, oldLevel, newLevel));
    }
    
    public static Map<String, Integer> getCollections() {
        return new HashMap<>(collections);
    }
    
    public static Integer getLevel(String collection) {
        return collections.getOrDefault(ChatLib.toSnakeCase(collection), -1);
    }
    
    public static int getUnlocked(int tier) {
        return (int) collections.values().stream()
                .filter(level -> level != null && level >= tier)
                .count();
    }
    
    public static int getUnlocked() {
        return getUnlocked(0);
    }

    public static void reset() {
        collections.clear();
    }
}