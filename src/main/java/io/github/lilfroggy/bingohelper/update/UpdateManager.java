package io.github.lilfroggy.bingohelper.update;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.lilfroggy.bingohelper.BingoHelper;
import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.JoinHypixelEventBus;
import io.github.lilfroggy.bingohelper.events.WorldChangeEventBus;
import io.github.lilfroggy.bingohelper.http.HttpUtils;
import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.Bingo;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Logger;
import io.github.lilfroggy.bingohelper.util.Version;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class UpdateManager {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final int HOURS_BEFORE_BINGO_TO_PERFORM_CHECK = 4;
    private static final String DOWNLOAD_COMMAND = "bh";
    public static final String LATEST_RELEASE_URL = "https://api.github.com/repos/LilFroggy/BingoHelper/releases/latest";
    public static final String MAINTENANCE_URL = "https://github.com/LilFroggy/BingoHelper/blob/main/docs/Maintenance.md";

    @Nullable
    public static UpdateInfo latestRelease = UpdateInfo.fromString(Config.updateInfo);
    private static UpdateState state = latestRelease != null ? latestRelease.STATE : UpdateState.NONE;

    public static void init() {
        JoinHypixelEventBus.register(UpdateManager::onJoinHypixel);
        WorldChangeEventBus.register(UpdateManager::onWorldChange);
    }

    public static void onJoinHypixel(boolean isAlpha) {
        check(true);
    }

    public static void onWorldChange(MinecraftClient client, ClientWorld world) {
        attemptLastMinuteCheck();
    }

    private static void attemptLastMinuteCheck() {
        if (!Bingo.startsInLessThanXHours(HOURS_BEFORE_BINGO_TO_PERFORM_CHECK)) {
            if (Config.performedLastMinuteCheck) {
                Config.performedLastMinuteCheck = false;
                Config.save();
            }
            return;
        }
        
        if (Config.performedLastMinuteCheck) return;

        if (Config.debug) Logger.info("Performing last minute update check");
        
        check();
        Config.performedLastMinuteCheck = true;
        Config.save();
    }

    public static void check() {
        check(false);
    }

    public static void check(boolean sendNotificationRegardless) {
        if (state == UpdateState.CHECKING) return;
        UpdateState previousState = state;
        setState(UpdateState.CHECKING);

        HttpUtils.sendAsyncWithEtag(LATEST_RELEASE_URL, Config.latestReleaseETag,
            () -> {
                setState(previousState);
                if (sendNotificationRegardless) sendUpdateNotification(state);
            },
            data -> {
                Config.latestReleaseETag = data.ETAG;
                Config.save();

                JsonObject release = JsonParser.parseString(data.BODY).getAsJsonObject();
                JsonArray assets = release.getAsJsonArray("assets");
                if (assets.isEmpty()) return;
                JsonObject asset = assets.get(0).getAsJsonObject();
                
                String latestChangelog = release.get("body").getAsString();
                String latestFileName = asset.get("name").getAsString();
                Version latestVersion = new Version(latestFileName.replace("BingoHelper-", "").replace(".jar", ""));
                String latestDownloadUrl = asset.get("browser_download_url").getAsString();

                Version currentVersion = BingoHelper.VERSION;

                boolean isIncompatible = !latestVersion.FULL.contains(Version.SEPARATOR + currentVersion.MC);
                boolean isNew = !currentVersion.FULL.equals(latestVersion.FULL);

                if (isIncompatible) setState(UpdateState.INCOMPATIBLE);
                else if (isNew) setState(UpdateState.AVAILABLE);
                else setState(UpdateState.NONE);
                
                latestRelease = new UpdateInfo(state, latestFileName, latestVersion, latestDownloadUrl, latestChangelog);

                Config.updateInfo = latestRelease.toString();
                Config.save();

                if (isIncompatible || isNew) sendUpdateNotification(state);
            },
            statusCode -> {
                setState(previousState);
            },
            e -> {
                setState(UpdateState.PARSE_ERROR);
            }
        );
    }

    public static void download() {
        if (latestRelease == null || state != UpdateState.AVAILABLE) return;
        setState(UpdateState.DOWNLOADING);

        CompletableFuture.runAsync(() -> {
            try {
                URL url = URI.create(latestRelease.DOWNLOAD_URL).toURL();
                File modsFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "mods");
                
                File targetFile = new File(modsFolder, latestRelease.FILE_NAME);
    
                try (InputStream in = url.openStream()) {
                    Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                File currentJar = new File(UpdateManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                
                currentJar.deleteOnExit();

                Config.updateInfo = ""; // So state is NONE instead of AVAILABLE on next launch
                Config.save();
                setState(UpdateState.DOWNLOADED);
            } catch (Exception e) {
                Logger.error("Error downloading update", e);
                setState(UpdateState.DOWNLOAD_ERROR);
            }
        });
    }

    private static void sendUpdateNotification(UpdateState state) {
        if (latestRelease == null || latestRelease.VERSION == null) return;

        Version current = BingoHelper.VERSION;
        Version latest = latestRelease.VERSION;

        if (state == UpdateState.AVAILABLE) {
            String msg = Messages.UPDATE_AVAILABLE.formatted(current.MOD, latest.MOD);
            ChatLib.chatClickableCommand(msg, DOWNLOAD_COMMAND);
        }
        else if (state == UpdateState.INCOMPATIBLE) {
            String msg = Messages.MANUAL_UPDATE_REQUIRED.formatted(current.MC, latest.MC);
            ChatLib.chatClickableUrl(msg, MAINTENANCE_URL);
        }
    }

    // Only one listener to prevent memory leak
    private static Consumer<UpdateState> stateChangeListener;

    public static void onStateChange(Consumer<UpdateState> callback) {
        stateChangeListener = callback;
        CLIENT.send(() -> stateChangeListener.accept(state));
    }

    public static void setState(UpdateState newState) {
        if (state == newState) return;
        state = newState;
        if (stateChangeListener == null) return;
        CLIENT.send(() -> stateChangeListener.accept(state));
    }

    public static UpdateState getState() {
        return state;
    }
}