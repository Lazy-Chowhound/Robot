package com.demo.szp.ChattingRobot.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.demo.szp.ChattingRobot.R;
import com.demo.szp.ChattingRobot.database.DbManager;
import com.demo.szp.ChattingRobot.model.HttpUtils;
import com.demo.szp.ChattingRobot.model.Msg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText msg_box;
    private Button btnSend;
    private RecyclerView rvChat;
    private ImageView imageView;

    private ChatAdapter chatAdapter;
    private ArrayList<Msg> list;

    private String json;
    private String text;
    private String url;

    private DbManager dbManager;
    private Date date;
    private SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DbManager(getApplicationContext());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        setContentView(R.layout.activity_main);

        initView();
        btnSend.setOnClickListener(this);

//        imageView = (ImageView) findViewById(R.id.remove);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dbManager.delete_record();
//            }
//        });
    }

    private void initView() {
        msg_box = (EditText) findViewById(R.id.msg_box);
        btnSend = (Button) findViewById(R.id.btn_send);

        rvChat = (RecyclerView) findViewById(R.id.rv_chat);
        rvChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list = new ArrayList<>();


        date = new Date(System.currentTimeMillis());

        list.add(new Msg("你好", 1));
        dbManager.insert_record("你好", 1, simpleDateFormat.format(date));
        list.add(new Msg("我是对话机器人", 1));
        dbManager.insert_record("我是对话机器人", 1, simpleDateFormat.format(date));
        list.add(new Msg("你想聊些什么", 1));
        dbManager.insert_record("你想聊些什么", 1, simpleDateFormat.format(date));

        // 加载之前的对话记录
        dbManager.search_record(list);

        chatAdapter = new ChatAdapter(this, list);
        rvChat.setAdapter(chatAdapter);
        //图灵API参数
        json = "{\"perception\":{\"inputText\":{\"text\":\"[*]\"}},\"userInfo\":{\"apiKey\":\"5aab776a08fb4940a9df4515d21b85f3\",\"userId\":\"helloRobot\"}}";
    }

    @Override
    public void onClick(View v) {
        list.add(new Msg(msg_box.getText().toString(), 0));
        chatAdapter.notifyItemInserted(chatAdapter.getItemCount() - 1);
        rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        json = json.replaceAll("[*]", msg_box.getText().toString());

        date = new Date(System.currentTimeMillis());
        dbManager.insert_record(msg_box.getText().toString(), 0, simpleDateFormat.format(date));

        msg_box.setText("");
        new Thread() {
            @Override
            public void run() {
                try {
                    text = new HttpUtils().sendPost("http://openapi.tuling123.com/openapi/api/v2", json);
                    Log.i("JSON", json);
                    readJSON(text);
                    list.add(new Msg(text, 1));

                    date = new Date(System.currentTimeMillis());
                    dbManager.insert_record(text, 1, simpleDateFormat.format(date));

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
