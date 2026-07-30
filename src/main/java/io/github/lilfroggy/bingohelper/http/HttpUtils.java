package io.github.lilfroggy.bingohelper.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import io.github.lilfroggy.bingohelper.util.Logger;

public class HttpUtils {
    private static final Minecraft MINECRAFT_CLIENT = Minecraft.getInstance();

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
                Logger.debug("Data is §cunchanged§r for: §7" + url);
                onUnchanged.run();
            }
            else if (response.statusCode() == 200) {
                Logger.debug("Data §achanged§r for: §7" + url);
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

    public static void sendAsync(String url, Consumer<HttpResponse<String>> onSuccess) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        sendAsync(request, onSuccess, error -> {});
    }

    public static void sendAsync(String url, Consumer<HttpResponse<String>> onSuccess, Consumer<Throwable> onError) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        sendAsync(request, onSuccess, onError);
    }

    public static void sendAsync(HttpRequest request, Consumer<HttpResponse<String>> onSuccess, Consumer<Throwable> onError) {
        HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                MINECRAFT_CLIENT.schedule(() -> {
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
                MINECRAFT_CLIENT.schedule(() -> onError.accept(t));
                return null;
            });
    }
}