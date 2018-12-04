package com.demo.szp.ChattingRobot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    public void insert_record(String mytext, int mytype, String mydate) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("text", mytext);
        contentValues.put("type", mytype);
        contentValues.put("create_time", mydate);
        sqLiteDatabase.insert("record", null, contentValues);
    }

    public void search_record(ArrayList<Msg> list) {
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

    public void delete_record() {
        String sql = "DELETE FROM record";
        sqLiteDatabase.execSQL(sql);
        sql = "UPDATE sqlite_sequence SET seq = 0 WHERE name='record'";
        sqLiteDatabase.execSQL(sql);
    }
}
