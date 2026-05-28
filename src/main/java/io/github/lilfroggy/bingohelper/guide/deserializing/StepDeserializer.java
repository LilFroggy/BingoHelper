package io.github.lilfroggy.bingohelper.guide.deserializing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.guide.step.impl.*;
import io.github.lilfroggy.bingohelper.guide.step.properties.bingoRanks.BingoRanksProperty;
import io.github.lilfroggy.bingohelper.util.Deserializer;
import net.minecraft.util.math.Vec3d;

public class StepDeserializer {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Vec3d.class, new Vec3dAdapter())
        .registerTypeAdapter(BingoRanksProperty.class, new RanksPropertyAdapter())
        .registerTypeAdapter(
            Step.class,
            new Deserializer<Step>("type")
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
                .register("clickSlot", ClickSlotStep.class)
                .register("store", StoreStep.class)
                .register("retrieve", RetrieveStep.class)
                .register("experience", ExperienceStep.class)
                .register("skill", SkillStep.class)
                .register("collection", CollectionStep.class)
                .register("upgradeMinion", UpgradeMinionStep.class)
                .register("mobTypes", MobTypesStep.class)
        )
        .create();

    private static final Step MALFORMED_STEP = stepFromJson("{\"type\": \"message\",\"instruction\": \"&cThis step is malformed.\n&cRun &e/bhskip &cto skip it.\",\"criteria\": \"kdasndlqwdn\"}");

    @SuppressWarnings({ "null", "unused" })
    public static Step stepFromJson(String step) {
        Step processed = GSON.fromJson(step, Step.class);
        if (processed == null) return MALFORMED_STEP;
        processed.init();
        return processed;
    }
}