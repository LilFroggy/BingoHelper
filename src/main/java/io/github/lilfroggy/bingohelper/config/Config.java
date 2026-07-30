package io.github.lilfroggy.bingohelper.config;

import gg.essential.universal.UScreen;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Category;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.PropertyType;
import gg.essential.vigilance.data.SortingBehavior;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPlayerInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Config extends Vigilant {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    public static Config getInstance() {
        return INSTANCE;
    } 

    // CREDITS

    @Property(
        type = PropertyType.BUTTON,
        name = "§7Vigilance",
        description = "Available under the LGPL 3.0 License",
        placeholder = "Source",
        category = "Credits"
    )
    public static void vigilanceSource() {
        openLink("https://github.com/EssentialGG/Vigilance");
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "§7JSON Schema Validator",
        description = "Available under the Apache License 2.0",
        placeholder = "Source",
        category = "Credits"
    )
    public static void jsonSchemaValidatorSource() {
        openLink("https://github.com/networknt/json-schema-validator");
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "§7Skyblocker",
        description = "Source for some rendering, scoreboard, and tablist logic, modified as needed",
        placeholder = "Source",
        category = "Credits"
    )
    public static void skyblockerSource() {
        openLink("https://github.com/SkyblockerMod/Skyblocker");
    }

    // FEATURES

    @Property(
        type = PropertyType.SWITCH,
        name = "Guide",
        description = "Display bingo guide",
        category = "Guide"
    )
    public static boolean guide = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Auto Import",
        description = "Automatically import official guides",
        category = "Guide"
    )
    public static boolean autoImport = true;

    /*@Property(
        type = PropertyType.SWITCH,
        name = "Exit Menus",
        description = "Close gui menus on step completion.",
        category = "Guide"
    )
    public static boolean exitMenus = false;*/

    @Property(
        type = PropertyType.TEXT,
        name = "Visit Island",
        description = "Player to visit for cakes and other buffs",
        category = "Guide",
        placeholder = "Empty :("
    )
    public static String visitIsland = "BingoHelper";

    @Property(
        type = PropertyType.SWITCH,
        name = "Instructions Only",
        description = "Hides extra info about the guide",
        category = "Guide"
    )
    public static boolean instructionsOnly = false;

    @Property(
        type = PropertyType.SWITCH,
        name = "Puzzler Solver",
        description = "Show solution to puzzler's quest",
        category = "Other"
    )
    public static boolean puzzlerSolver = true;

    // PERSISTENT DATA

    @Property(
        type = PropertyType.TEXT,
        name = "Latest Release ETag",
        category = "Dev",
        hidden = true
    )
    public static String latestReleaseETag = "";

    @Property(
        type = PropertyType.TEXT,
        name = "Latest Guide ETag",
        category = "Dev",
        hidden = true
    )
    public static String latestGuideETag = "";

    @Property(
        type = PropertyType.TEXT,
        name = "Update Info",
        category = "Dev",
        hidden = true
    )
    public static String updateInfo = "";

    @Property(
        type = PropertyType.SWITCH,
        name = "Performed Last Minute Check",
        category = "Dev",
        hidden = true
    )
    public static boolean performedLastMinuteCheck = true;

    // DEV STUFF

    @Property(
        type = PropertyType.SELECTOR,
        name = "Enable Bingo Features",
        description = "",
        category = "Dev",
        options = {"On Bingo", "On Ironman", "Always"}
    )
    public static int gamemodeIndex = 0;

    @Property(
        type = PropertyType.NUMBER,
        name = "Bingo Rank",
        category = "Dev",
        min = -1,
        max = Integer.MAX_VALUE
    )
    public static int bingoRank = -1;

    @Property(
        type = PropertyType.TEXT,
        name = "Hypixel Rank",
        category = "Dev"
    )
    public static String hypixelRank = "";

    @Property(
        type = PropertyType.BUTTON,
        name = "",
        category = "Dev",
        placeholder = "Update Hypixel Rank"
    )
    public static void updateHypixelRank() {
        HypixelModAPI.getInstance().sendPacket(new ServerboundPlayerInfoPacket());
    }

    @Property(
        type = PropertyType.SWITCH,
        name = "Validate Guides",
        description = "§cMay cause crashes if disabled!",
        category = "Dev"
    )
    public static boolean validateGuides = true;

    @Property(
        type = PropertyType.SWITCH,
        name = "Debug",
        category = "Dev"
    )
    public static boolean debug = false;

    @Property(
        type = PropertyType.TEXT,
        name = "Json Indent",
        category = "Dev"
    )
    public static String jsonIndent = "\\t";

    public static final Config INSTANCE = new Config(); // Needs to be at the bottom or the default values take priority

    public Config() {
        super(
            new File("./config/bingohelper/config.toml"),
            "BingoHelper",
            new JVMAnnotationPropertyCollector(),
            new SortingBehavior() {
                @Override
                public Comparator<Category> getCategoryComparator() {
                    List<String> categories = Arrays.asList("Guide", "Other", "Dev", "Credits");
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
    }

    public static void save() {
        INSTANCE.markDirty();
        INSTANCE.writeData();
    }

    public static void open() {
        CLIENT.schedule(() -> UScreen.displayScreen(Config.INSTANCE.gui()));
    }

    public static void openLink(String url) {
        Util.getPlatform().openUri(url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Object instance, String fieldName) throws Exception {
        Field delegateField = instance.getClass().getDeclaredField(fieldName);
        delegateField.setAccessible(true);
        Object delegate = delegateField.get(instance);

        Field valueField = delegate.getClass().getSuperclass().getDeclaredField("value");
        valueField.setAccessible(true);

        return (T) valueField.get(delegate);
    }

    public static void init() {}
}