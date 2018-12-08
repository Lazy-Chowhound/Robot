package com.demo.szp.ChattingRobot.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.szp.ChattingRobot.R;
import com.demo.szp.ChattingRobot.model.Msg;

import java.util.ArrayList;
import java.util.List;

public class Search_chapter extends RecyclerView.Adapter {
    private Context context;

    private static final int ME = 0;
    private static final int OTHRE = 1;
    private static final int NONEITEM = 2;

    private List<Msg> list;

    public Search_chapter(Context context, ArrayList<Msg> list) {
        this.context = context;
        this.list = list;
    }

    class searchViewHolder extends RecyclerView.ViewHolder {

        LinearLayout me;
        LinearLayout other;
        LinearLayout noneitem;

        public searchViewHolder(View itemView) {
            super(itemView);
            me = itemView.findViewById(R.id.search_me);
            other = itemView.findViewById(R.id.search_other);
            noneitem = itemView.findViewById(R.id.none_area);
        }

        public LinearLayout getMe() {
            return me;
        }

        public LinearLayout getOther() {
            return other;
        }

        public LinearLayout getNone() {
            return noneitem;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Search_chapter.searchViewHolder viewHolder = null;

        switch (viewType) {
            case ME:
                viewHolder = new Search_chapter.searchViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item4, parent, false));
                break;
            case OTHRE:
                viewHolder = new Search_chapter.searchViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item3, parent, false));
                break;
            case NONEITEM:
                viewHolder = new Search_chapter.searchViewHolder(LayoutInflater.from(context).inflate(R.layout.none_item, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Search_chapter.searchViewHolder viewHolder = (Search_chapter.searchViewHolder) holder;

        TextView tv = new TextView(context);

        Msg msg = list.get(position);

        tv.setText(msg.getMsg());
        tv.setAutoLinkMask(Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        switch (msg.getType()) {
            case ME:
                viewHolder.getMe().removeAllViews();
                viewHolder.getMe().addView(tv);
                break;
            case OTHRE:
                viewHolder.getOther().removeAllViews();
                viewHolder.getOther().addView(tv);
                break;
            case NONEITEM:
                viewHolder.getNone().removeAllViews();
                viewHolder.getNone().addView(tv);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = list.get(position).getType();
        if (type == 0) {
            return ME;
        } else if (type == 1) {
            return OTHRE;
        } else {
            return NONEITEM;
        }
    }
}
