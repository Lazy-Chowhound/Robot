package com.demo.sisyphus.hellorobot.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Sisyphus on 2017/3/2.
 */
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

    /***
     * post请求数据
     * @param url 请求地址
     * @param keys 请求参数的key值数组
     * @param values 请求参数的key对应的value数组
     * @return 返回请求结果
     * @throws IOException
     */
    public String sendPost(String url, String json) throws IOException {
        client = new OkHttpClient();
//        FormBody.Builder builder = new FormBody.Builder();
//        for (int i = 0; i < keys.length; i++) {
//            builder.add(keys[i], values[i]);
//        }
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
