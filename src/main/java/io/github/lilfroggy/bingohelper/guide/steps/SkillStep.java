package io.github.lilfroggy.bingohelper.guide.steps;

import io.github.lilfroggy.bingohelper.events.SkillUpdateEventBus;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.Bingo;

public class SkillStep extends Step implements SkillUpdateEventBus.SkillUpdateListener {
    public String skill;
    public Integer level;

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
        Integer currentLevel = Bingo.getSkillLevel(skill);
        if (currentLevel != null && currentLevel >= level) Guide.advance();
    }

    @Override
    protected void onDeactivate() {
        SkillUpdateEventBus.unregister(this);
    }

    @Override
    public void onSkillUpdate(String updatedSkill, Integer previousLevel, Integer newLevel) {
        //ChatLib.chat(updatedSkill + "\n" + previousLevel + "\n" + newLevel);
        if (!skill.equals(updatedSkill)) return;
        if (newLevel < level) return;
        Guide.advance();
    }
    
}