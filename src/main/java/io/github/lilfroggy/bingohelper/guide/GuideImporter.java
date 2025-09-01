package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.ClipboardUtils;
import io.github.lilfroggy.bingohelper.util.FileLib;
import io.github.lilfroggy.bingohelper.util.Logger;

public class GuideImporter {
    private static final String SAVE_FILE_PATH = "config/bingohelper/guide.json";

    public static void importGuide(String guide) {
        GuideData data = GuideParser.toGuideData(guide);
        if (data == null) return;
        GuideInfo old = new GuideInfo(Guide.name, Guide.version);
        setGuideData(data);
        GuideNavigator.reset();
        GuideSaver.save(data.raw());
        ChatLib.chatWithPrefix(importMessage(old, data));
    }

    public static void importFromSaveFile() {
        String saved = FileLib.read(SAVE_FILE_PATH);
        GuideData data = GuideParser.toGuideData(saved);
        if (data == null) return;
        GuideInfo old = new GuideInfo(Guide.name, Guide.version);
        setGuideData(data);
        GuideNavigator.goToStep(Config.savedIndex);
        GuideSaver.save(data.raw());
        Logger.info(importMessage(old, data), true);
    }

    public static void importFromClipboard() {
        importGuide(ClipboardUtils.getClipboard());
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