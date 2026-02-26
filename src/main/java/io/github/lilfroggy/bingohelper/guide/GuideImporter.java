package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.FileLib;
import io.github.lilfroggy.bingohelper.util.Logger;

public class GuideImporter {

    public static void importGuide(String guide) {
        GuideData data = GuideParser.toGuideData(guide);
        if (data == null) return;
        GuideInfo old = new GuideInfo(Guide.name, Guide.version);
        setGuideData(data);
        GuideNavigator.reset();
        GuideSaver.saveActiveGuide(data.raw());
        ChatLib.chat(importMessage(old, data));
    }

    public static void importFromSave() {
        String saved = FileLib.read(GuideSaver.ACTIVE_SAVE_PATH);
        GuideData data = GuideParser.toGuideData(saved);
        if (data == null) return;
        GuideInfo old = new GuideInfo(Guide.name, Guide.version);
        setGuideData(data);
        GuideNavigator.goToStep(Config.savedIndex);
        Logger.info(importMessage(old, data), true);
    }

    public static void importFromClipboard() {
        String clipboard = ClipboardUtils.getClipboard();
        if (!GuideValidator.isValidGuide(clipboard)) return;
        importGuide(clipboard);
        Config.latestGuideETag = ""; // Clear ETag so we still receive updates
        Config.save();
        if (!Config.autoImport) return;
        Config.autoImport = false;
        Config.save();
        ChatLib.chat(Messages.GUIDE_AUTO_IMPORT_DISABLED);
    }

    private static void setGuideData(GuideData data) {
        Guide.name = data.name();
        Guide.version = data.version();
        Guide.stepIndex = data.stepIndex();
        Guide.steps = data.steps();
    }

    private static String importMessage(GuideInfo oldGuide, GuideData newGuide) {
        return Messages.GUIDE_IMPORT.formatted(oldGuide.name(), oldGuide.version(), newGuide.name(), newGuide.version());
    }
}