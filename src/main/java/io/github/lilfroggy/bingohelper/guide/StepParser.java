package io.github.lilfroggy.bingohelper.guide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.lilfroggy.bingohelper.guide.steps.*;
import io.github.lilfroggy.bingohelper.util.RuntimeTypeAdapterFactory;

class StepParser {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(Step.class, "type")
                .registerSubtype(MessageStep.class, "message")
                .registerSubtype(CakeStep.class, "cake")
                .registerSubtype(SellStep.class, "sell")
                .registerSubtype(BuyStep.class, "buy")
                .registerSubtype(HasStep.class, "has")
                .registerSubtype(AreaStep.class, "area")
                .registerSubtype(SubAreaStep.class, "subArea")
                .registerSubtype(EnchantStep.class, "enchant")
                .registerSubtype(ReforgeStep.class, "reforge")
                .registerSubtype(GuiItemStep.class, "guiItem")
                .registerSubtype(GuiClickSlotStep.class, "guiClickSlot")
                .registerSubtype(StoreStep.class, "store")
                .registerSubtype(RetrieveStep.class, "retrieve")
                .registerSubtype(ExperienceStep.class, "experience")
                .registerSubtype(SkillStep.class, "skill")
                .registerSubtype(CollectionStep.class, "collection")
                .registerSubtype(UpgradeMinionStep.class, "upgradeMinion")
        )
    .create();

    public static Step stepFromJson(String step) {
        return GSON.fromJson(step, Step.class);
    }
}