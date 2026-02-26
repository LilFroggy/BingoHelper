package io.github.lilfroggy.bingohelper.guide;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.http.HttpUtils;
import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.FileLib;

public class GuideUpdater {
    private static final String GUIDE_URL = "https://raw.githubusercontent.com/LilFroggy/BingoHelper-REPO/master/guides/latest.json";
    private static final String UPDATE_COMMAND = "bhupdateguide";

    public static void onJoinHypixel(boolean isAlpha) {
        check(Config.autoImport);
    }

    public static void check(boolean update) {
        HttpUtils.sendAsyncWithEtag(GUIDE_URL, Config.latestGuideETag,
            () -> {
                String latest = FileLib.read(GuideSaver.LATEST_SAVE_PATH);
                GuideData data = GuideParser.toGuideData(latest);
                if (data == null) return;
                if (!isNewGuide(data.name(), data.version())) return;
                notifyOrUpdate(data, update);
            },
            resData -> {
                Config.latestGuideETag = resData.ETAG;
                Config.save();
                GuideData data = GuideParser.toGuideData(resData.BODY);
                GuideSaver.saveLatestGuide(resData.BODY);
                notifyOrUpdate(data, update);
            },
            statusCode -> {},
            e -> {}
        );
    }

    public static void update(GuideData data) {
        GuideImporter.importGuide(data.raw());
    }

    private static void notifyOrUpdate(GuideData data, boolean update) {
        if (update) update(data);
        else sendUpdateNotification(data);
    }

    private static boolean isNewGuide(String name, Integer version) {
        if (name == null || version == null) return false;
        return !Guide.name.equals(name) || Guide.version != version;
    }

    private static void sendUpdateNotification(GuideData data) {
        ChatLib.chatClickableCommand(Messages.GUIDE_UPDATE_AVAILABLE.formatted(data.name(), data.version(), UPDATE_COMMAND), UPDATE_COMMAND);
    }
}