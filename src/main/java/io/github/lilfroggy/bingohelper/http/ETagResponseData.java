package io.github.lilfroggy.bingohelper.http;

import java.net.http.HttpResponse;

public class ETagResponseData {
    public final HttpResponse<String> RESPONSE;
    public final String ETAG;
    public final String BODY;

    ETagResponseData(HttpResponse<String> response, String etag, String body) {
        this.RESPONSE = response;
        this.ETAG = etag;
        this.BODY = body;
    }
}