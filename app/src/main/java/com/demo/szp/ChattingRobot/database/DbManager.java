package com.demo.szp.ChattingRobot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.demo.szp.ChattingRobot.model.Msg;

import java.util.ArrayList;


public class DbManager {
    DatabaseHelper databaseHelper;
    SQLiteDatabase sqLiteDatabase;

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

    public ArrayList<Msg> search_record() {
        ArrayList<Msg> list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query("record", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.move(i);
                String text = cursor.getString(cursor.getColumnIndex("text"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                list.add(new Msg(text, type));
            }
        }
        return list;
    }

    public void delete_record() {
        Cursor cursor = sqLiteDatabase.query("record", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 1; i <= cursor.getCount(); i++) {
                sqLiteDatabase.delete("record", "id=", new String[]{String.valueOf(i)});
            }
        }
    }
}
