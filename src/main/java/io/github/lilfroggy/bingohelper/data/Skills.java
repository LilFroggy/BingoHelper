package io.github.lilfroggy.bingohelper.data;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Skyblock;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class Skills {
    private static final Pattern SKILL_UNLOCK_REGEX = Pattern.compile("^SKILL LEVEL UP (.+) I$");
    private static final Pattern SKILL_LEVEL_UP_REGEX = Pattern.compile("^SKILL LEVEL UP (.+) (.+)➜(.+)$");
    private static final String CATACOMBS_UNLOCK_MESSAGE = "DUNGEON LEVEL UP The Catacombs I";
    private static final Pattern CATACOMBS_LEVEL_UP_REGEX = Pattern.compile("^DUNGEON LEVEL UP The Catacombs (.+)➜(.+)$");

    private static final Pattern SKILL_NORMAL_PROGRESS_REGEX = Pattern.compile("\\+(?<gain>[\\d.,]+) (?<name>.+) \\((?<current>[\\d.,]+)\\/(?<needed>[\\d,.]+[kmb]?)\\)");
    private static final Pattern SKILL_PERCENTAGE_PROGRESS_REGEX = Pattern.compile("\\+(?<gain>[\\d.,]+) (?<name>.+) \\((?<percent>[\\d.,]+)%\\)");
    
    private static final Map<String, Double> skills = new HashMap<>();

    public static void init() {
        Events.CREATE_BINGO_PROFILE.register(Skills::onCreateBingoProfile);
        Events.MESSAGE.register(Skills::onGameMessage);
        Events.ACTION_BAR_MESSAGE.register(Skills::onActionBarMessage);
    }

    public static void onCreateBingoProfile() {
        reset();
        if (Config.debug) Logger.info("Skills reset");
    }
    
    public static void onGameMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!Skyblock.inBingo()) return;

        String msg = unformattedMsg.trim();
        
        Matcher skillUnlock = SKILL_UNLOCK_REGEX.matcher(msg);
        Matcher skillLevelUp = SKILL_LEVEL_UP_REGEX.matcher(msg);
        Matcher catacombsLevelUp = CATACOMBS_LEVEL_UP_REGEX.matcher(msg);

        if (skillUnlock.matches()) set(skillUnlock.group(1), 1.0);
        else if (skillLevelUp.matches()) set(skillLevelUp.group(1), skillLevelUp.group(3));
        else if (msg.equals(CATACOMBS_UNLOCK_MESSAGE)) set("catacombs", 1.0);
        else if (catacombsLevelUp.matches()) set("catacombs", catacombsLevelUp.group(2));
    }

    public static void onActionBarMessage(String formattedMsg, String unformattedMsg, CallbackInfo ci) {
        if (!Skyblock.inBingo()) return;
        
        Matcher percentage = SKILL_PERCENTAGE_PROGRESS_REGEX.matcher(unformattedMsg);
        Matcher normal = SKILL_NORMAL_PROGRESS_REGEX.matcher(unformattedMsg);
    
        if (percentage.find()) {
            String name = percentage.group("name");
            double percent = Double.parseDouble(percentage.group("percent"));
            double currentIntLevel = Math.floor(getLevel(name));
            double preciseLevel = currentIntLevel + (percent / 100.0);
            set(name, preciseLevel);
        }
        else if (normal.find()) {
            String name = normal.group("name");
            double currentXp = Double.parseDouble(normal.group("current").replace(",", ""));
            double neededXp = ChatLib.parseKMB(normal.group("needed")); 
            double currentIntLevel = Math.floor(getLevel(name));
            double preciseLevel = currentIntLevel + (currentXp / neededXp);
            set(name, preciseLevel);
        }
    }

    public static void set(String skill, double level) {
        String id = ChatLib.toSnakeCase(skill);
        double oldLevel = getLevel(id);
        if (oldLevel == level) return;
        skills.put(id, level);
        if (Config.debug) Logger.info("Skill updated: " + id + ": " + oldLevel + " -> " + level);
        Events.LEVEL_SKILL.invoke(listener -> listener.onLevelSkill(id, oldLevel, level));
    }

    public static void set(String skill, String level) {
        Integer decoded = ChatLib.decodeNumeral(level.toString());
        if (decoded == null) return;
        set(skill, (double) decoded);
    }
    
    public static Map<String, Double> getSkills() {
        return new HashMap<>(skills);
    }
    
    public static double getLevel(String skill) {
        return skills.getOrDefault(ChatLib.toSnakeCase(skill), 0.0);
    }

    public static void reset() {
        skills.clear();
    }
}