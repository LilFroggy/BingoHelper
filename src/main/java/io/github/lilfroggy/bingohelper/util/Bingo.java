package io.github.lilfroggy.bingohelper.util;

import io.github.lilfroggy.bingohelper.events.ChatEventBus;
import io.github.lilfroggy.bingohelper.events.SkillUpdateEventBus;
import io.github.lilfroggy.bingohelper.events.CollectionUpdateEventBus;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Bingo {
    private static final Pattern SKILL_UNLOCK_REGEX = Pattern.compile("^SKILL LEVEL UP (.+) I$");
    private static final Pattern SKILL_LEVEL_UP_REGEX = Pattern.compile("^SKILL LEVEL UP (.+) (.+)➜(.+)$");
    private static final Pattern CATACOMBS_LEVEL_UP_REGEX = Pattern.compile("^DUNGEON LEVEL UP The Catacombs (.+)➜(.+)$");
    private static final Pattern COLLECTION_UNLOCK_REGEX = Pattern.compile("^COLLECTION UNLOCKED (.+)$");
    private static final Pattern COLLECTION_TIER_ONE_REGEX = Pattern.compile("^COLLECTION LEVEL UP (.+) (.+)$");
    private static final Pattern COLLECTION_LEVEL_UP_REGEX = Pattern.compile("^COLLECTION LEVEL UP (.+) (.+)➜(.+)$");
    
    private static final String BINGO_JOIN_MESSAGE = "Welcome to SkyBlock Bingo!";
    private static final String CATACOMBS_UNLOCK_MESSAGE = "DUNGEON LEVEL UP The Catacombs I";
    
    private static final Map<String, Integer> skills = new HashMap<>();
    private static final Map<String, Integer> collections = new HashMap<>();

    public static void init() {
        ChatEventBus.register(Bingo::onGameMessage);
    }
    
    public static void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {

        if (!MinecraftClient.getInstance().isOnThread()) return;

        String msg = unformattedMsg.trim();

        // Join Bingo
        if (msg.equals(BINGO_JOIN_MESSAGE)) {
            reset();
            return;
        }
        
        // Skill Level Up
        Matcher skillUnlock = SKILL_UNLOCK_REGEX.matcher(msg);
        Matcher skillLevelUp = SKILL_LEVEL_UP_REGEX.matcher(msg);
        Matcher catacombsLevelUp = CATACOMBS_LEVEL_UP_REGEX.matcher(msg);
        
        if (skillUnlock.matches()) {
            updateSkill(skillUnlock.group(1), 1);
            return;
        }
        
        if (skillLevelUp.matches()) {
            updateSkill(skillLevelUp.group(1), skillLevelUp.group(3));
            return;
        }
        
        if (msg.equals(CATACOMBS_UNLOCK_MESSAGE)) {
            updateSkill("catacombs", 1);
            return;
        }
        
        if (catacombsLevelUp.matches()) {
            updateSkill("catacombs", catacombsLevelUp.group(2));
            return;
        }
        
        // Collection Level Up
        Matcher collectionUnlock = COLLECTION_UNLOCK_REGEX.matcher(msg);
        Matcher collectionTierOne = COLLECTION_TIER_ONE_REGEX.matcher(msg);
        Matcher collectionLevelUp = COLLECTION_LEVEL_UP_REGEX.matcher(msg);
        
        if (collectionUnlock.matches()) {
            updateCollection(collectionUnlock.group(1), 0);
            return;
        }
        
        if (collectionLevelUp.matches()) {
            updateCollection(collectionLevelUp.group(1), collectionLevelUp.group(3));
            return;
        }
        
        if (collectionTierOne.matches()) {
            updateCollection(collectionTierOne.group(1), collectionTierOne.group(2));
            return;
        }
    }
    
    private static void updateSkill(String skill, Object level) {
        //String originalSkill = skill;
        skill = skill.replaceAll(" ", "_").toLowerCase();
        Integer skillLevel = ChatLib.decodeNumeral(level.toString());
        if (skillLevel != null) {
            Integer previousLevel = skills.get(skill);
            //ChatLib.chat("[BH DEBUG] Skill level up detected: " + originalSkill + " (key: " + skill + ") from " + previousLevel + " to " + skillLevel);
            skills.put(skill, skillLevel);
            
            SkillUpdateEventBus.fire(skill, previousLevel, skillLevel);
        }
    }
    
    private static void updateCollection(String collection, Object level) {
        //String originalCollection = collection;
        collection = collection.replaceAll(" ", "_").toLowerCase();
        Integer collectionLevel = ChatLib.decodeNumeral(level.toString());
        if (collectionLevel != null) {
            Integer previousLevel = collections.get(collection);
            //ChatLib.chat("[BH DEBUG] Collection level up detected: " + originalCollection + " (key: " + collection + ") from " + previousLevel + " to " + collectionLevel);
            collections.put(collection, collectionLevel);
            
            CollectionUpdateEventBus.fire(collection, previousLevel, collectionLevel);
        }
    }
    
    private static void reset() {
        skills.clear();
        collections.clear();
        Logger.info("Bingo data reset", true);
    }
    
    public static Map<String, Integer> getSkills() {
        return new HashMap<>(skills);
    }
    
    public static Map<String, Integer> getCollections() {
        return new HashMap<>(collections);
    }
    
    public static Integer getSkillLevel(String skill) {
        return skills.get(skill.toLowerCase().replaceAll(" ", "_"));
    }
    
    public static Integer getCollectionLevel(String collection) {
        return collections.get(collection.toLowerCase().replaceAll(" ", "_"));
    }
    
    public static int getCollectionsUnlocked(int tier) {
        return (int) collections.values().stream()
                .filter(level -> level != null && level >= tier)
                .count();
    }
    
    public static int getCollectionsUnlocked() {
        return getCollectionsUnlocked(0);
    }
}