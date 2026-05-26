package io.github.lilfroggy.bingohelper.guide.deserializing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.guide.step.impl.*;
import io.github.lilfroggy.bingohelper.util.PolymorphicDeserializer;
import net.minecraft.util.math.Vec3d;

public class StepDeserializer {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Vec3d.class, new Vec3dAdapter())
        .registerTypeAdapter(
            Step.class,
            new PolymorphicDeserializer<Step>("type")
                .register("message", MessageStep.class)
                .register("cake", CakeStep.class)
                .register("sell", SellStep.class)
                .register("buy", BuyStep.class)
                .register("has", HasStep.class)
                .register("area", AreaStep.class)
                .register("subArea", SubAreaStep.class)
                .register("enchant", EnchantStep.class)
                .register("reforge", ReforgeStep.class)
                .register("guiItem", GuiItemStep.class)
                .register("guiClickSlot", GuiClickSlotStep.class)
                .register("store", StoreStep.class)
                .register("retrieve", RetrieveStep.class)
                .register("experience", ExperienceStep.class)
                .register("skill", SkillStep.class)
                .register("collection", CollectionStep.class)
                .register("upgradeMinion", UpgradeMinionStep.class)
                .register("mobType", MobTypeStep.class)
        )
        .create();

    public static Step stepFromJson(String step) {
        Step processed = GSON.fromJson(step, Step.class);
        processed.init();
        return processed;
    }
}