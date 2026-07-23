package io.github.lilfroggy.bingohelper.guide.step.impl;

import io.github.lilfroggy.bingohelper.data.Skills;
import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.LevelSkillEvent;
import io.github.lilfroggy.bingohelper.guide.step.Step;

public class SkillStep extends Step implements LevelSkillEvent {
    public String skill;
    public double level;

    @Override
    public String locallyFormatted() {
        return instruction;
    }

    @Override
    public void onInit() {
        // Nothing to reset
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        Events.LEVEL_SKILL.register(this);

        double currentLevel = Skills.getLevel(skill);
        if (currentLevel >= level) complete(); // initial check
    }

    @Override
    protected void onDeactivate() {
        Events.LEVEL_SKILL.unregister(this);
    }

    @Override
    public void onLevelSkill(String updatedSkill, double previousLevel, double newLevel) {
        if (!skill.equals(updatedSkill)) return;
        if (newLevel >= level) complete();
    }
    
}