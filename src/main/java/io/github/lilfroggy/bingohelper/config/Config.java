package io.github.lilfroggy.bingohelper.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Category;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import gg.essential.vigilance.data.SortingBehavior;
import io.github.lilfroggy.bingohelper.guide.Guide;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Config extends Vigilant {

    @Property(
        type = PropertyType.SWITCH,
        name = "Guide",
        description = "Display a step-by-step guide for bingo.",
        category = "General"
    )
    public static boolean guide = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Auto Import",
        description = "Import latest official guide when joining Hypixel.",
        category = "General"
    )
    public static boolean autoImport = true;

    /*@Property(
        type = PropertyType.SWITCH,
        name = "Exit Menus",
        description = "Close gui menus on step completion.",
        category = "General"
    )
    public static boolean exitMenus = false;*/

    @Property(
        type = PropertyType.TEXT,
        name = "Visit Island",
        description = "Player to visit for cakes and other buffs.",
        category = "General",
        placeholder = "Empty :("
    )
    public static String visitIsland = "BingoHelper";

    @Property(
        type = PropertyType.NUMBER,
        name = "Saved Index",
        description = "§cSubmit a bug report if you see this!",
        category = "Dev",
        min = 0,
        max = Integer.MAX_VALUE,
        hidden = true
    )
    public static int savedIndex = 0;

    @Property(
        type = PropertyType.SELECTOR,
        name = "Enable Bingo Features",
        description = "",
        category = "Dev",
        options = {"On Bingo", "On Ironman", "Always"}
    )
    public static int gamemodeIndex = 0;

    @Property(
        type = PropertyType.TEXT,
        name = "Guide URL",
        description = "§cProceed with caution!",
        category = "Dev",
        placeholder = "Empty :("
    )
    public static String guideUrl = "https://raw.githubusercontent.com/LilFroggy/BingoHelper-REPO/master/guides/carries.json";

    @Property(
        type = PropertyType.SWITCH,
        name = "Debug",
        category = "Dev"
    )
    public static boolean debug = false;

    public static final Config INSTANCE = new Config(); // Needs to be at the bottom or the default values take priority

    public Config() {
        super(
            new File("./config/bingohelper/config.toml"),
            "BingoHelper",
            new JVMAnnotationPropertyCollector(),
            new SortingBehavior() {
                @Override
                public Comparator<Category> getCategoryComparator() {
                    List<String> categories = Arrays.asList("General", "Dev");
                    return (a, b) -> categories.indexOf(a.getName()) - categories.indexOf(b.getName());
                }
            }
        );

        initialize();

        registerListener("guide", (state) -> {
            if ((boolean) state) Guide.currentStep.activate();
            else Guide.currentStep.deactivate();
        });
    }

    public static void save() {
        INSTANCE.markDirty();
        INSTANCE.writeData();
    }

    public static void init() {
        // Load config before anything checks them
    }
}