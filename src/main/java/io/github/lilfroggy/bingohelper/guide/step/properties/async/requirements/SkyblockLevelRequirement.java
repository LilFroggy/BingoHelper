package io.github.lilfroggy.bingohelper.guide.step.properties.async.requirements;

import io.github.lilfroggy.bingohelper.events.Events;
import io.github.lilfroggy.bingohelper.events.interfaces.SkyblockLevelChangeEvent;
import io.github.lilfroggy.bingohelper.util.Skyblock;

public class SkyblockLevelRequirement implements Requirement, SkyblockLevelChangeEvent {

    private Runnable onChange;
    private Integer skyblockLevel;

    public SkyblockLevelRequirement(Integer skyblockLevel) {
        this.skyblockLevel = skyblockLevel;
    }

    @Override
    public void register(Runnable onChange) {
        if (skyblockLevel == null) return;
        this.onChange = onChange;
        Events.SKYBLOCK_LEVEL_CHANGE.register(this);
    }

    @Override
    public void unregister() {
        if (skyblockLevel == null) return;
        Events.SKYBLOCK_LEVEL_CHANGE.unregister(this);
    }

    @Override
    public boolean isMet() {
        return skyblockLevel == null || Skyblock.level() >= skyblockLevel;
    }

    @Override
    public void onSkyblockLevelChange(int level) {
        onChange.run();
    }

    @Override
    public String toString() {
        return "SkyblockLevelRequirement{" +
                "skyblockLevel=" + skyblockLevel +
                ", userLevel=" + Skyblock.level() +
                ", isMet=" + isMet() +
                '}';
    }
}