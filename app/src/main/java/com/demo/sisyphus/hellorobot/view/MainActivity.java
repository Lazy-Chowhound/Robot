package com.demo.sisyphus.hellorobot.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.demo.sisyphus.hellorobot.R;
import com.demo.sisyphus.hellorobot.model.HttpUtils;
import com.demo.sisyphus.hellorobot.model.Msg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText msg_box;
    private Button btnSend;
    private RecyclerView rvChat;

    private ChatAdapter chatAdapter;
    private ArrayList<Msg> list;

    private String json;
    private String text;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        btnSend.setOnClickListener(this);
    }

    private void initView() {
        msg_box = (EditText) findViewById(R.id.msg_box);
        btnSend = (Button) findViewById(R.id.btn_send);

        rvChat = (RecyclerView) findViewById(R.id.rv_chat);
        rvChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list = new ArrayList<>();
        list.add(new Msg("你好", 1));
        list.add(new Msg("我是对话机器人", 1));
        list.add(new Msg("你想聊些什么", 1));

        chatAdapter = new ChatAdapter(this, list);
        rvChat.setAdapter(chatAdapter);

        json = "{\"perception\":{\"inputText\":{\"text\":\"[*]\"}},\"userInfo\":{\"apiKey\":\"5aab776a08fb4940a9df4515d21b85f3\",\"userId\":\"helloRobot\"}}";
    }

    @Override
    public void onClick(View v) {
        list.add(new Msg(msg_box.getText().toString(), 0));
        chatAdapter.notifyItemInserted(chatAdapter.getItemCount() - 1);
        rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        json = json.replaceAll("[*]", msg_box.getText().toString());
        msg_box.setText("");
        new Thread() {
            @Override
            public void run() {
                try {
                    text = new HttpUtils().sendPost("http://openapi.tuling123.com/openapi/api/v2", json);
                    Log.i("JSON",json);
                    readJSON(text);
                    list.add(new Msg(text, 1));
                    if (url != null) {
                        list.add(new Msg(url, 1));
                    }
                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    public void readJSON(String strJson) {
        Log.i("strJson", strJson);
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i <= jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String values = jsonObject1.getString("values");
                JSONObject textJson = new JSONObject(values);
                text = textJson.getString("text");
                url = jsonObject1.getString("url");
            }
        } catch (JSONException e) {
            url = null;
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    chatAdapter.notifyItemInserted(chatAdapter.getItemCount() - 1);
                    rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    break;
            }
        }
    };
}
