package com.demo.szp.ChattingRobot.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.demo.szp.ChattingRobot.R;
import com.demo.szp.ChattingRobot.database.DbManager;
import com.demo.szp.ChattingRobot.model.HttpUtils;
import com.demo.szp.ChattingRobot.model.Msg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText msg_box;
    private Button btnSend;
    private ImageView imageView_remove;
    private ImageView imageView_search;
    private ImageView imageView_picture;

    private RecyclerView rvChat;
    private ChatAdapter chatAdapter;
    private ArrayList<Msg> list;

    //图灵API参数
    private String json = "{\"perception\":{\"inputText\":{\"text\":\"[*]\"}},\"userInfo\":{\"apiKey\":\"5aab776a08fb4940a9df4515d21b85f3\",\"userId\":\"helloRobot\"}}";
    private String send_json;
    private String text;
    private String record;
    private int position;

    private DbManager dbManager;
    private Date date;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.mipmap.background);
        dbManager = new DbManager(getApplicationContext());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        list = new ArrayList<>();

        setContentView(R.layout.activity_main);
        msg_box = findViewById(R.id.msg_box);
        btnSend = findViewById(R.id.btn_send);
        rvChat = findViewById(R.id.rv_chat);
        imageView_remove = findViewById(R.id.remove);
        imageView_search = findViewById(R.id.search);
        imageView_picture = findViewById(R.id.picture);
        initView();

        btnSend.setOnClickListener(this);
        imageView_search.setOnClickListener(this);
        imageView_remove.setOnClickListener(this);
        imageView_picture.setOnClickListener(this);

        // 软键盘弹出时recyclerView上移
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);


        rvChat.addOnItemTouchListener(new RecyclerViewClick(this, rvChat, new RecyclerViewClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String urltext = list.get(position).getMsg();
                if (Patterns.WEB_URL.matcher(urltext).matches() || URLUtil.isValidUrl(urltext)) {
                    imageView_picture.setX(220);
                    imageView_picture.setY(200);
                    imageView_picture.setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext()).load(urltext).into(imageView_picture);
                    Toast.makeText(getApplicationContext(), "再次点击图片退出", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("是否删除这条消息");
                builder.setNegativeButton("否", null);
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int total = chatAdapter.getItemCount();
                        Log.i("total", "" + total);
                        dbManager.delete_record(position + 1, total);
                        list.remove(position);
                        chatAdapter.notifyItemRemoved(position);
                    }
                });
                builder.show();
            }
        }));
    }

    private void initView() {
        rvChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // 加载之前的对话记录
        dbManager.search_all_record(list);

        date = new Date(System.currentTimeMillis());
        list.add(new Msg("你好", 1));
        dbManager.insert_record("你好", 1, simpleDateFormat.format(date));
        list.add(new Msg("我是对话机器人", 1));
        dbManager.insert_record("我是对话机器人", 1, simpleDateFormat.format(date));

        chatAdapter = new ChatAdapter(this, list);
        rvChat.setAdapter(chatAdapter);
        rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                list.add(new Msg(msg_box.getText().toString(), 0));
                chatAdapter.notifyItemInserted(chatAdapter.getItemCount() - 1);
                rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

                send_json = json.replaceAll("[*]", msg_box.getText().toString());
                date = new Date(System.currentTimeMillis());
                dbManager.insert_record(msg_box.getText().toString(), 0, simpleDateFormat.format(date));

                msg_box.setText("");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            text = new HttpUtils().sendPost("http://openapi.tuling123.com/openapi/api/v2", send_json);
                            Log.i("JSON", send_json);
                            readJSON(text);
                            list.add(new Msg(text, 1));
                            rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

                            date = new Date(System.currentTimeMillis());
                            dbManager.insert_record(text, 1, simpleDateFormat.format(date));
                        } catch (IOException e) {
                            Log.e("IOException", e.toString());
                        }
                        handler.sendEmptyMessage(0);
                    }
                }.start();
                break;
            case R.id.remove:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("确认");
                builder.setMessage("是否删除所有记录?");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbManager.delete_all_record();
                        //调整显示
                        int count = chatAdapter.getItemCount();
                        list.clear();
                        chatAdapter.notifyItemRangeRemoved(0, count);
                        rvChat.smoothScrollToPosition(0);
                        Toast toast = Toast.makeText(getApplicationContext(), "清空消息成功", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
                builder.setNegativeButton("否", null);
                builder.show();
                break;
            case R.id.search:
                final Intent intent = new Intent(MainActivity.this, Search_Activity.class);
                final EditText editText = new EditText(this);
                final AlertDialog.Builder input = new AlertDialog.Builder(this);
                input.setTitle("输入想查找的消息");
                input.setView(editText);
                input.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int search_count = 0;
                        record = editText.getText().toString();
                        for (position = 0; position < list.size(); position++) {
                            Msg item = list.get(position);
                            if (item.getMsg().contains(record)) {
                                intent.putExtra("" + search_count, item.getMsg());
                                intent.putExtra("" + search_count + "type", item.getType());
                                search_count++;
                            }
                        }
                        startActivity(intent);
                    }
                });
                input.setNegativeButton("取消", null);
                input.show();
                break;
            case R.id.picture:
                imageView_picture.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void readJSON(String strJson) {
        try {
            Log.i("answer", strJson);
            JSONObject jsonObject = new JSONObject(strJson);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String values = jsonObject1.getString("values");
                JSONObject textJson = new JSONObject(values);
                text = textJson.getString("text");
            }
        } catch (Exception e) {
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
