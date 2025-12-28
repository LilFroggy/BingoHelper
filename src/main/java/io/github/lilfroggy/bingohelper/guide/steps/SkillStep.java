package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.data.Skills;
import io.github.lilfroggy.bingohelper.events.SkillUpdateEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;

public class SkillStep extends Step implements SkillUpdateEventBus.SkillUpdateListener {
    public String skill;
    public double level;

    @Override
    public String additionalInstructionFormatting() {
        return instruction;
    }

    @Override
    public void onReset() {
        // Nothing to reset
    }

    @Override
    protected void onActivate() {
        SkillUpdateEventBus.register(this);

        // Need initial check
        double currentLevel = Skills.getLevel(skill);
        if (currentLevel >= level) Guide.advance();
    }

    @Override
    protected void onDeactivate() {
        SkillUpdateEventBus.unregister(this);
    }

    @Override
    public void onSkillUpdate(String updatedSkill, double previousLevel, double newLevel) {
        if (!skill.equals(updatedSkill)) return;
        if (newLevel < level) return;
        Guide.advance();
    }
    
}