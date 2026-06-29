package io.github.lilfroggy.bingohelper.util.item;

import java.util.List;

public class EnchantInfo {
    public String id;
    public List<String> requiredEnchants;
    public boolean done;

    public EnchantInfo(String id, List<String> requiredEnchants) {
        this.id = id;
        this.requiredEnchants = requiredEnchants;
    }

    public String id() {
        return id;
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