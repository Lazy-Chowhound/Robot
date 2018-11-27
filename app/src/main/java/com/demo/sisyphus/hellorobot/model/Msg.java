package com.demo.sisyphus.hellorobot.model;

/**
 * Created by Sisyphus on 2017/3/1.
 */
public class Msg {

    private String msg;
    private int type;

    public Msg(String msg, int type){
        this.msg = msg;
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public int getType() {
        return type;
    }
}
