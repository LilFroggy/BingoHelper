package io.github.lilfroggy.bingohelper.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

import io.github.lilfroggy.bingohelper.config.Config;
import io.github.lilfroggy.bingohelper.util.Logger;
import net.minecraft.client.MinecraftClient;

public class HttpUtils {
    private static final MinecraftClient MINECRAFT_CLIENT = MinecraftClient.getInstance();

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public static void sendAsyncWithEtag(String url, String etag, Runnable onUnchanged, Consumer<ETagResponseData> onChanged, Consumer<Integer> onBadRequest, Consumer<Throwable> onError) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).GET();

        if (etag != null && !etag.isEmpty()) {
            builder.header("If-None-Match", etag);
        }
    
        sendAsync(builder.build(), response -> {
            if (response.statusCode() == 304) {
                Logger.info("Data is §cunchanged§r for: §7" + url, !Config.debug);
                onUnchanged.run();
            }
            else if (response.statusCode() == 200) {
                Logger.info("Data §achanged§r for: §7" + url, !Config.debug);
                String newETag = response.headers().firstValue("ETag").orElse(null);
                ETagResponseData data = new ETagResponseData(response, newETag, response.body());
                onChanged.accept(data);
            }
            else {
                int code = response.statusCode();
                Logger.warn("Received status code " + code + " from " + url);
                onBadRequest.accept(response.statusCode());
            }
        }, onError);
    }

    public static void sendAsync(String url, Consumer<HttpResponse<String>> onSuccess, Consumer<Throwable> onError) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        sendAsync(request, onSuccess, onError);
    }

    public static void sendAsync(HttpRequest request, Consumer<HttpResponse<String>> onSuccess, Consumer<Throwable> onError) {
        HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                MINECRAFT_CLIENT.send(() -> {
                    try {
                        onSuccess.accept(response);
                    } catch (Exception e) {
                        Logger.error("Error handling data from " + request.uri(), e);
                        onError.accept(e);
                    }
                });
            })
            .exceptionally(t -> {
                Exception e = new Exception(t);
                Logger.error("Network error for " + request.uri(), e);
                MINECRAFT_CLIENT.send(() -> onError.accept(t));
                return null;
            });
    }
}