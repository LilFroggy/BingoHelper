package io.github.lilfroggy.bingohelper.util.item;

public class HasInfo {
    public int count;
    public int target;
    public boolean done;

    public HasInfo(Integer target) {
        this.target = target;
    }

    public int count() {
        return count;
    }

    public int target() {
        return target;
    }

    public boolean done() {
        return done;
    }

    public boolean hasEnough() {
        return count >= target;
    }

    public void reset() {
        count = 0;
        done = false;
    }
}