package com.demo.szp.ChattingRobot.view;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.szp.ChattingRobot.R;
import com.demo.szp.ChattingRobot.model.Msg;

import java.util.ArrayList;

public class Search_Activity extends AppCompatActivity {
    private RecyclerView rvChat;
    private Search_chapter search_chapter;
    private ArrayList<Msg> list;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        list = new ArrayList<>();
        rvChat = (RecyclerView) findViewById(R.id.search_chat);
        rvChat.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        imageView = (ImageView) findViewById(R.id.return_arrow);
        textView = (TextView) findViewById(R.id.return_to);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_Activity.this.finish();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_Activity.this.finish();
            }
        });

        initView();
    }

    private void initView() {
        rvChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Bundle bundle = getIntent().getExtras();
        try {
            int size = bundle.size();
            Log.i("size", "" + size);
            for (int i = 0; i < size / 2; i++) {
                String text = bundle.getString("" + i);
                int type = bundle.getInt("" + i + "type");
                list.add(new Msg(text, type));
            }
        } catch (Exception e) {
            list.add(new Msg("无相关记录", 2));
        }
        search_chapter = new Search_chapter(this, list);
        rvChat.setAdapter(search_chapter);
        rvChat.smoothScrollToPosition(0);
    }
}
