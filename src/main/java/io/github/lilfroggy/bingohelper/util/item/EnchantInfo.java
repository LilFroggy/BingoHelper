package io.github.lilfroggy.bingohelper.util.item;

import java.util.List;

public class EnchantInfo {
    public List<String> requiredEnchants;
    public boolean done;

    public EnchantInfo(List<String> requiredEnchants) {
        this.requiredEnchants = requiredEnchants;
    }

    public List<String> requiredEnchants() {
        return requiredEnchants;
    }

    public boolean done() {
        return done;
    }

    public void reset() {
        done = false;
    }
}