package io.github.lilfroggy.bingohelper.guide.deserializing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.lilfroggy.bingohelper.guide.step.Step;
import io.github.lilfroggy.bingohelper.guide.step.impl.*;
import io.github.lilfroggy.bingohelper.guide.step.properties.bingoRanks.BingoRanksProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.navTo.NavToProperty;
import io.github.lilfroggy.bingohelper.guide.step.properties.prerequisites.PrerequisitesProperty;
import io.github.lilfroggy.bingohelper.util.Deserializer;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.item.EnchantList;
import io.github.lilfroggy.bingohelper.util.item.HasList;
import io.github.lilfroggy.bingohelper.util.item.ReforgeList;
import net.minecraft.world.phys.Vec3;

public class StepDeserializer {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(HasList.class, new ItemListAdapter())
        .registerTypeAdapter(ReforgeList.class, new ReforgeListAdapter())
        .registerTypeAdapter(EnchantList.class, new EnchantListAdapter())
        .registerTypeAdapter(Vec3.class, new Vec3dAdapter())
        .registerTypeAdapter(BingoRanksProperty.class, new RanksPropertyAdapter())
        .registerTypeAdapter(NavToProperty.class, new NavToPropertyAdapter())
        .registerTypeAdapter(PrerequisitesProperty.class, new PrerequisitesPropertyAdapter())
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
                .register("supercraft", SupercraftStep.class)
        )
        .create();

    private static final Step MALFORMED_STEP = stepFromJson("{\"type\": \"message\",\"instruction\": \"&cThis step is malformed.\n&cRun &e/bhskip &cto skip it.\",\"criteria\": \"kdasndlqwdn\"}");

    @SuppressWarnings({ "null", "unused" })
    public static Step stepFromJson(String step) {
        try {
            Step processed = GSON.fromJson(step, Step.class);
            if (processed == null) return MALFORMED_STEP;
            processed.init();
            return processed;
        } catch (Exception e) {
            Logger.error("error parsing step", e);
            return MALFORMED_STEP;
        }
    }
}