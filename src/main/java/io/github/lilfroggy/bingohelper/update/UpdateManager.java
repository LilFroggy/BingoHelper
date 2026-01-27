package io.github.lilfroggy.bingohelper.update;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.events.FirstJoinServerEventBus;
import io.github.lilfroggy.bingohelper.messages.Messages;
import io.github.lilfroggy.bingohelper.util.ChatLib;
import io.github.lilfroggy.bingohelper.util.Constants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class UpdateManager {
    private record ReleaseInfo(String fileName, String version, String downloadUrl, String changelog, boolean compatible) {}

    public enum UpdateState {
        NONE, CHECKING, AVAILABLE, DOWNLOADING, DOWNLOADED, INCOMPATIBLE;
    
        public void updateButtons() {
            Config.showCheckButton = (this == NONE || this == CHECKING);
            Config.showDownloadButton = (this == AVAILABLE || this == DOWNLOADING);
            Config.showRestartButton = (this == DOWNLOADED);
            Config.showGitHubButton = (this == INCOMPATIBLE);
            Config.save();
        }
    }
    
    private static final String DOWNLOAD_COMMAND = "bh";
    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/LilFroggy/BingoHelper/releases/latest";
    
    public static ReleaseInfo latestRelease;
    private static UpdateState state = UpdateState.NONE;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final HttpRequest LATEST_RELEASE_REQUEST = HttpRequest.newBuilder()
            .uri(URI.create(LATEST_RELEASE_URL))
            .header("User-Agent", "BingoHelper-Updater")
            .build();

    public static void init() {
        state.updateButtons();
        FirstJoinServerEventBus.register(UpdateManager::onFirstJoinServer);
    }

    public static void onFirstJoinServer(MinecraftClient client) {
        checkForUpdate();
    }

    public static void checkForUpdate() {
        if (state == UpdateState.CHECKING) return;
        setState(UpdateState.CHECKING);

        setUpdateStatus("§fChecking...");

        HTTP_CLIENT.sendAsync(LATEST_RELEASE_REQUEST, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(json -> {
                JsonObject release = JsonParser.parseString(json).getAsJsonObject();

                JsonArray assets = release.getAsJsonArray("assets");
                if (!(assets.size() > 0)) return;
                JsonObject asset = assets.get(0).getAsJsonObject();
                
                String latestChangelog = release.get("body").getAsString();
                String latestFileName = asset.get("name").getAsString();
                String latestVersion = latestFileName.replace("BingoHelper-", "").replace(".jar", "");
                String latestDownloadUrl = asset.get("browser_download_url").getAsString();

                String currentModVersion = Constants.MOD_VERSION;
                String currentMcVersion = Constants.MC_VERSION;

                boolean isCompatible = latestVersion.contains("mc" + currentMcVersion);

                latestRelease = new ReleaseInfo(latestFileName, latestVersion, latestDownloadUrl, latestChangelog, isCompatible);

                if (!isCompatible) {
                    String[] parts = latestVersion.split("-mc");
                    String latestMcVersion = (parts.length > 1) ? parts[1] : "UNKNOWN";
                    setUpdateStatus("§eUnsupported Minecraft version. Update to " + latestMcVersion);
                    ChatLib.chatClickableWithPrefix(Messages.UNSUPPORTED_MINECRAFT_VERSION.formatted(latestMcVersion), DOWNLOAD_COMMAND, "/" + DOWNLOAD_COMMAND);
                    setState(UpdateState.INCOMPATIBLE);
                } else if (!currentModVersion.equals(latestVersion)) {
                    String currentVersionSimple = currentModVersion.split("-")[0];
                    String latestVersionSimple = latestVersion.split("-")[0];
                    setUpdateStatus("§aUpdate available: §c" + currentVersionSimple + " ➜ " + "§a" + latestVersionSimple);
                    ChatLib.chatClickableWithPrefix(Messages.MOD_UPDATE_AVAILABLE.formatted(latestVersionSimple, DOWNLOAD_COMMAND), DOWNLOAD_COMMAND, "/" + DOWNLOAD_COMMAND);
                    setState(UpdateState.AVAILABLE);
                } else {
                    setUpdateStatus("§aBingoHelper is up to date.");
                    setState(UpdateState.NONE);
                }

            }).exceptionally(e -> {
                setUpdateStatus("§cError checking for update. Try again later.");
                setState(UpdateState.NONE);
                e.printStackTrace();
                return null;
            });
    }

    public static void downloadUpdate() {
        if (latestRelease == null || state == UpdateState.DOWNLOADING) return;
        setState(UpdateState.DOWNLOADING);
        
        setUpdateStatus("§fDownloading...");

        CompletableFuture.runAsync(() -> {
            try {
                URL url = URI.create(latestRelease.downloadUrl()).toURL();
                File modsFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "mods");
                
                File targetFile = new File(modsFolder, latestRelease.fileName());
    
                try (InputStream in = url.openStream()) {
                    Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                File currentJar = new File(UpdateManager.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI());
                
                currentJar.deleteOnExit();

                setUpdateStatus("§aDownload complete. Restart to apply changes.");
                setState(UpdateState.DOWNLOADED);
            } catch (Exception e) {
                setUpdateStatus("§cError downloading update.");
                setState(UpdateState.NONE);
            }
        });
    }

    public static void setUpdateStatus(String status) {
        Config.INSTANCE.setCategoryDescription("About", status);
        Config.refreshUI();
    }

    public static String formatChangelog(String input) {
        if (input == null || input.isEmpty()) return "";
    
        String text = input.replace("\r\n", "\n");
        text = text.replaceFirst("(?m)^#.*\\n*", "");
        text = text.replaceAll("(?m)^#+\\s*", "§f");
        return text.trim();
    }

    public static void setState(UpdateState newState) {
        state = newState;
        state.updateButtons();
    }

    public static UpdateState getState() {
        return state;
    }
}