package io.github.lilfroggy.bingohelper.util.item;

import java.util.List;

public class ReforgeInfo {
    public List<String> validReforges;
    public boolean done;

    public ReforgeInfo(List<String> validReforges) {
        this.validReforges = validReforges;
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
        return reforge != null && validReforges.contains(reforge);
    }
}