package io.github.lilfroggy.bingohelper.events.interfaces;

public interface LevelSkillEvent {
    void onLevelSkill(String skill, double previousLevel, double newLevel);
}