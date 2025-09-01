package io.github.lilfroggy.bingohelper.guide;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Scheduler;
import net.minecraft.client.MinecraftClient;

public class GuideUpdater {
    private static final String GUIDE_URL = Config.guideUrl;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final HttpRequest GUIDE_REQUEST = HttpRequest.newBuilder().uri(URI.create(GUIDE_URL)).build();
    private static final String UPDATE_COMMAND = "bhupdate";

    public static void onFirstJoinServer(MinecraftClient client) {
        Scheduler.SCHEDULER.schedule(() -> {
            client.execute(() -> {
                if (Config.debug) Logger.info("Attempting to update...");
                if (Config.autoImport) GuideUpdater.update();
                else GuideUpdater.checkForUpdate();
            });
        }, 3, TimeUnit.SECONDS);
    }

    public static void checkForUpdate() {
        GuideData data = fetchGuideData();
        if (!isNewGuide(data)) return;
        ChatLib.chatClickableWithPrefix(Messages.GUIDE_UPDATE_AVAILABLE.formatted(data.name(), data.version(), UPDATE_COMMAND), UPDATE_COMMAND, "/" + UPDATE_COMMAND);
    }

    public static void update() {
        GuideData data = fetchGuideData();
        if (!isNewGuide(data)) return;
        GuideImporter.importGuide(data.raw());
    }

    private static GuideData fetchGuideData() {
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(GUIDE_REQUEST, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new Exception("HTTP " + response.statusCode());
            return GuideParser.toGuideData(response.body());
        } catch (Exception e) {
            Logger.error("Error fetching guide", e);
            return null;
        }
    }

    private static boolean isNewGuide(GuideData data) {
        if (data == null) return false;
        return !Guide.name.equals(data.name()) || Guide.version != data.version();
    }
}