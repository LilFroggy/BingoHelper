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
    
    // Regex patterns for parsing messages
    private static final Pattern SKILL_UNLOCK_REGEX = Pattern.compile("^SKILL LEVEL UP (.+) I$");
    private static final Pattern SKILL_LEVEL_UP_REGEX = Pattern.compile("^SKILL LEVEL UP (.+) (.+)➜(.+)$");
    private static final Pattern CATACOMBS_LEVEL_UP_REGEX = Pattern.compile("^DUNGEON LEVEL UP The Catacombs (.+)➜(.+)$");
    private static final Pattern COLLECTION_UNLOCK_REGEX = Pattern.compile("^COLLECTION UNLOCKED (.+)$");
    private static final Pattern COLLECTION_TIER_ONE_REGEX = Pattern.compile("^COLLECTION LEVEL UP (.+) (.+)$");
    private static final Pattern COLLECTION_LEVEL_UP_REGEX = Pattern.compile("^COLLECTION LEVEL UP (.+) (.+)➜(.+)$");
    
    private static final String BINGO_JOIN_MESSAGE = "Welcome to SkyBlock Bingo!";
    private static final String CATACOMBS_UNLOCK_MESSAGE = "DUNGEON LEVEL UP The Catacombs I";
    
    // Skill and collection data
    private static final Map<String, Integer> skills = new HashMap<>();
    private static final Map<String, Integer> collections = new HashMap<>();
    
    static {
        initializeDefaultData();
        ChatEventBus.register(Bingo::onGameMessage);
    }

    public static void init() {
        // Load
    }
    
    private static void initializeDefaultData() {
        // Initialize skills
        skills.put("combat", null);
        skills.put("farming", null);
        skills.put("fishing", null);
        skills.put("mining", null);
        skills.put("foraging", null);
        skills.put("enchanting", null);
        skills.put("alchemy", null);
        skills.put("carpentry", null);
        skills.put("runecrafting", null);
        skills.put("taming", null);
        skills.put("social", null);
        skills.put("hunting", null);
        skills.put("catacombs", null);
        
        // Initialize collections - Farming
        collections.put("cactus", null);
        collections.put("carrot", null);
        collections.put("cocoa_beans", null);
        collections.put("feather", null);
        collections.put("leather", null);
        collections.put("melon", null);
        collections.put("mushroom", null);
        collections.put("mutton", null);
        collections.put("nether_wart", null);
        collections.put("potato", null);
        collections.put("pumpkin", null);
        collections.put("raw_chicken", null);
        collections.put("raw_porkchop", null);
        collections.put("raw_rabbit", null);
        collections.put("seeds", null);
        collections.put("sugar_cane", null);
        collections.put("wheat", null);
        
        // Initialize collections - Mining
        collections.put("coal", null);
        collections.put("cobblestone", null);
        collections.put("diamond", null);
        collections.put("emerald", null);
        collections.put("end_stone", null);
        collections.put("gemstone", null);
        collections.put("glacite", null);
        collections.put("glowstone_dust", null);
        collections.put("gold_ingot", null);
        collections.put("gravel", null);
        collections.put("hard_stone", null);
        collections.put("ice", null);
        collections.put("iron_ingot", null);
        collections.put("lapis_lazuli", null);
        collections.put("mithril", null);
        collections.put("mycelium", null);
        collections.put("nether_quartz", null);
        collections.put("netherrack", null);
        collections.put("obsidian", null);
        collections.put("red_sand", null);
        collections.put("redstone", null);
        collections.put("sand", null);
        collections.put("sulphur", null);
        collections.put("tungsten", null);
        collections.put("umber", null);
        
        // Initialize collections - Combat
        collections.put("blaze_rod", null);
        collections.put("bone", null);
        collections.put("chili_pepper", null);
        collections.put("ender_pearl", null);
        collections.put("ghast_tear", null);
        collections.put("gunpowder", null);
        collections.put("magma_cream", null);
        collections.put("rotten_flesh", null);
        collections.put("slimeball", null);
        collections.put("spider_eye", null);
        collections.put("string", null);
        
        // Initialize collections - Foraging
        collections.put("oak_log", null);
        collections.put("birch_log", null);
        collections.put("spruce_log", null);
        collections.put("dark_oak_log", null);
        collections.put("acacia_log", null);
        collections.put("jungle_log", null);
        collections.put("fig_log", null);
        collections.put("tender_wood", null);
        collections.put("mangrove_log", null);
        collections.put("vinesap", null);
        collections.put("lushlilac", null);
        collections.put("sea_lumies", null);
        
        // Initialize collections - Fishing
        collections.put("clay", null);
        collections.put("clownfish", null);
        collections.put("ink_sac", null);
        collections.put("lily_pad", null);
        collections.put("magmafish", null);
        collections.put("prismarine_crystals", null);
        collections.put("prismarine_shard", null);
        collections.put("pufferfish", null);
        collections.put("raw_fish", null);
        collections.put("raw_salmon", null);
        collections.put("sponge", null);
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
            
            // Fire skill update event
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
        // Reset skills
        for (String skill : skills.keySet()) {
            skills.put(skill, null);
        }
        
        // Reset collections
        for (String collection : collections.keySet()) {
            collections.put(collection, null);
        }
        
        System.out.println("Bingo data reset");
    }
    
    // Getter methods
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