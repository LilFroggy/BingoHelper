package io.github.lilfroggy.bingohelper.guide.step.components.async;

import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.util.entity.EntityPredicate;

public class Async {
    public Integer effectiveIndex;
    public EntityPredicate ifMob;

    public void init() {
        if (effectiveIndex == null) effectiveIndex = Integer.MAX_VALUE;
    }

    public void register() {
        if (ifMob != null) ifMob.register();
    }

    public void unregister() {
        if (ifMob != null) ifMob.unregister();
    }

    public boolean isBlocking() {
        return Guide.index() >= effectiveIndex;
    }

    public int effectiveIndex() {
        return effectiveIndex;
    }

    public boolean ifMob() {
        return ifMob == null || ifMob.hasMatch();
    }

    public boolean isHidden() {
        return !isBlocking() && !ifMob();
    }
}