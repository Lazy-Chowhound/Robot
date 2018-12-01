package com.demo.szp.ChattingRobot.model;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpUtils {

    private String strResponse;

    private OkHttpClient client;

    public String sendGet(String url) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Exception", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.cacheResponse() != null) {
                    strResponse = response.cacheResponse().toString();
                    Log.i("result", strResponse);
                }
            }
        });
        return strResponse;
    }

    public String sendPost(String url, String json) throws IOException {
        client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,json);
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
