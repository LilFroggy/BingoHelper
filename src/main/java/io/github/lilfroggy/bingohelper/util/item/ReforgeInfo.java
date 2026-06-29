package io.github.lilfroggy.bingohelper.util.item;

import java.util.List;

public class ReforgeInfo {
    public String id;
    public List<String> validReforges;
    public boolean done;

    public ReforgeInfo(String id, List<String> validReforges) {
        this.id = id;
        this.validReforges = validReforges;
    }

    public String id() {
        return id;
    }

    public List<String> validReforges() {
        return validReforges;
    }

    public boolean done() {
        return done;
    }

    public void reset() {
        done = false;
    }

    public boolean isValidReforge(String reforge) {
        if (reforge == null) return false;
        return validReforges.contains(reforge);
    }
}