package com.demo.szp.ChattingRobot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.ActionBarContextView;
import android.util.Log;

import com.demo.szp.ChattingRobot.model.Msg;

import java.util.ArrayList;


public class DbManager {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DbManager(Context context) {
        databaseHelper = new DatabaseHelper(context, "record", null, 1);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    /**
     * 插入一条记录
     *
     * @param mytext
     * @param mytype
     * @param mydate
     */
    public void insert_record(String mytext, int mytype, String mydate) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("text", mytext);
        contentValues.put("type", mytype);
        contentValues.put("create_time", mydate);
        sqLiteDatabase.insert("record", null, contentValues);
    }

    /**
     * 查找全部记录
     *
     * @param list
     */
    public void search_all_record(ArrayList<Msg> list) {
        Cursor cursor = sqLiteDatabase.query("record", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String text = cursor.getString(cursor.getColumnIndex("text"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            list.add(new Msg(text, type));
            Log.i("text", text);
            Log.i("type", "" + type);
        }
        cursor.close();
    }

    /**
     * 删除所有记录
     */
    public void delete_all_record() {
        String sql = "DELETE FROM record";
        sqLiteDatabase.execSQL(sql);
        // 将自增字段重置
        sql = "UPDATE sqlite_sequence SET seq = 0 WHERE name='record'";
        sqLiteDatabase.execSQL(sql);
    }

    /**
     * 删除某条记录
     *
     * @param id
     */
    public void delete_record(int id, int count) {
        // 删除该记录
        sqLiteDatabase.delete("record", "id =", new String[]{String.valueOf(id)});
//        //将剩下的记录id -1
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("id", "id" + 1);
//        sqLiteDatabase.update("record", contentValues, "id > ?", new String[]{String.valueOf(id)});
//        //自增字段重置
//        contentValues.clear();
//        contentValues.put("seq", count - 1);
//        sqLiteDatabase.update("record", contentValues, "name = ?", new String[]{"record"});
    }
}
