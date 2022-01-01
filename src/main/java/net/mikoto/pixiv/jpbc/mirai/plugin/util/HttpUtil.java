package net.mikoto.pixiv.jpbc.mirai.plugin.util;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 * @author mikoto
 * @date 2021/10/16 16:07
 */
public class HttpUtil {
    /**
     * Send a get request.
     * It will return a string.
     *
     * @param url The link of this request.
     * @return The string this request return.
     * @throws IOException IOException.
     */
    @NotNull
    public static String httpGet(String url) throws IOException {
        String result;
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        result = Objects.requireNonNull(response.body()).string();
        return result;
    }

    /**
     * Send a get request.
     * It will return a string.
     *
     * @param url The link of this request.
     * @return The string this request return.
     * @throws IOException IOException.
     */
    @NotNull
    public static byte[] httpGetBytes(String url) throws IOException {
        byte[] result;
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        result = Objects.requireNonNull(response.body()).bytes();
        return result;
    }
}
