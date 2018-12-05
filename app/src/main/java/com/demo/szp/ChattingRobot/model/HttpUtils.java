package com.demo.szp.ChattingRobot.model;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpUtils {

    private String strResponse;

    private OkHttpClient client;


    public String sendPost(String url, String json) throws IOException {
        client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException();
        }
        strResponse = response.body().string();
        return strResponse;
    }
}
