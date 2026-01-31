package io.github.lilfroggy.bingohelper.config;

import gg.essential.universal.UScreen;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Category;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.PropertyType;
import gg.essential.vigilance.data.SortingBehavior;
import gg.essential.vigilance.gui.SettingsGui;
import io.github.lilfroggy.bingohelper.guide.Guide;
import io.github.lilfroggy.bingohelper.update.UpdateManager;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Config extends Vigilant {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    @Property(
        type = PropertyType.SWITCH,
        name = "showCheckButton",
        category = "About",
        hidden = true
    )
    public static boolean showCheckButton = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "showDownloadButton",
        category = "About",
        hidden = true
    )
    public static boolean showDownloadButton = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "showRestartButton",
        category = "About",
        hidden = true
    )
    public static boolean showRestartButton = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "showGitHubButton",
        category = "About",
        hidden = true
    )
    public static boolean showGitHubButton = false;

    @Property(
        type = PropertyType.BUTTON,
        name = "Check for update",
        category = "About",
        placeholder = "§fCheck for update"
    )
    public static void checkForUpdate() {
        UpdateManager.checkForUpdate();
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Download update",
        category = "About",
        placeholder = "§fClick to download"
    )
    public static void downloadUpdate() {
        UpdateManager.downloadUpdate();
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Restart required",
        category = "About",
        placeholder = "§fClick to close Minecraft"
    )
    public static void quitGame() {
        mc.scheduleStop();
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "More info",
        category = "About",
        placeholder = "§fClick to open GitHub"
    )
    public static void openGitHub() {
        openLink("https://github.com/LilFroggy/BingoHelper/releases/latest/");
    }

    // CREDITS

    @Property(
        type = PropertyType.BUTTON,
        name = "§7Vigilance",
        description = "Available under the LGPL 3.0 License",
        placeholder = "Source",
        category = "About",
        subcategory = "Credits"
    )
    public static void vigilanceSource() {
        openLink("https://github.com/EssentialGG/Vigilance");
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "§7JSON Schema Validator",
        description = "Available under the Apache License 2.0",
        placeholder = "Source",
        category = "About",
        subcategory = "Credits"
    )
    public static void jsonSchemaValidatorSource() {
        openLink("https://github.com/networknt/json-schema-validator");
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "§7Skyblocker",
        description = "Source for some rendering, scoreboard, and tablist logic, modified as needed.",
        placeholder = "Source",
        category = "About",
        subcategory = "Credits"
    )
    public static void skyblockerSource() {
        openLink("https://github.com/SkyblockerMod/Skyblocker");
    }

    // FEATURES

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
        type = PropertyType.SWITCH,
        name = "Puzzler Solver",
        description = "Shows solution to puzzler's quest.",
        category = "Misc"
    )
    public static boolean puzzlerSolver = true;

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

    @Property(
        type = PropertyType.SWITCH,
        name = "Validate Guides",
        category = "Dev"
    )
    public static boolean validateGuides = true;

    public static final Config INSTANCE = new Config(); // Needs to be at the bottom or the default values take priority

    public Config() {
        super(
            new File("./config/bingohelper/config.toml"),
            "BingoHelper",
            new JVMAnnotationPropertyCollector(),
            new SortingBehavior() {
                @Override
                public Comparator<Category> getCategoryComparator() {
                    List<String> categories = Arrays.asList("About", "General", "Misc", "Dev", "Credits");
                    return (a, b) -> categories.indexOf(a.getName()) - categories.indexOf(b.getName());
                }
                @Override
                public Comparator<? super Map.Entry<String,? extends List<PropertyData>>> getSubcategoryComparator() {
                    List<String> order = Arrays.asList("Updates", "Changelog", "Credits");
                    return (a, b) -> order.indexOf(a.getKey()) - order.indexOf(b.getKey());
                }
            }
        );
        
        initialize();

        registerListener("guide", (state) -> {
            if ((boolean) state) Guide.currentStep.activate();
            else Guide.currentStep.deactivate();
        });

        addDependency("checkForUpdate", "showCheckButton");
        addDependency("downloadUpdate", "showDownloadButton");
        addDependency("quitGame", "showRestartButton");
        addDependency("openGitHub", "showGitHubButton");
    }

    public static void save() {
        INSTANCE.markDirty();
        INSTANCE.writeData();
    }

    public static void open() {
        mc.send(() -> UScreen.displayScreen(Config.INSTANCE.gui()));
    }

    public static void refreshUI() {
        if (mc.currentScreen instanceof SettingsGui) open();
    }

    public static void init() {}

    private static void openLink(String url) {
        net.minecraft.util.Util.getOperatingSystem().open(url);
    }
}