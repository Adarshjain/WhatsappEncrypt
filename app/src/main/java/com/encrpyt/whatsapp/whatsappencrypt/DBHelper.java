package com.encrpyt.whatsapp.whatsappencrypt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "chatManager";

    private static final String TABLE_NAME = "chatsAll";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_NAME = "name";
    private static final String KEY_CHAT = "chat";
    private static final String KEY_DIRECTION = "direc";
    private static final String KEY_NUMBER = "number";

    private static final String INDEX_TABLE_NAME = "indexing";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TIME + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_CHAT + " TEXT," + KEY_DIRECTION + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
        String INDEX_TABLE = "CREATE TABLE " + INDEX_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT," + KEY_CHAT + " TEXT," + KEY_NUMBER + " TEXT)";
        db.execSQL(INDEX_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INDEX_TABLE_NAME);
        onCreate(db);
    }

    public void addMessage(Message message) {
        if (message.getName().contains("@") || message.getName().contains(":")) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, message.getTime());
        values.put(KEY_NAME, message.getName());
        values.put(KEY_CHAT, message.getChat());
        values.put(KEY_DIRECTION, message.getDirection());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void addIndex(Message message) {
        if (message.getName().contains("@") || message.getName().contains(":")) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, message.getName());
        values.put(KEY_CHAT, message.getChat());
        values.put(KEY_NUMBER, message.getNumber());

        db.insert(INDEX_TABLE_NAME, null, values);
        db.close();
    }

    public List<Message> getAllIndex() {
        List<Message> indexList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + INDEX_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message contact = new Message("", cursor.getString(1), cursor.getString(2), "", cursor.getString(3));
                indexList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return indexList;
    }

    public void deleteIndexIfExists(String Name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(INDEX_TABLE_NAME, new String[]{KEY_ID, KEY_NAME}, KEY_NAME + "=?",
                new String[]{Name}, null, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            db.execSQL("delete from " + INDEX_TABLE_NAME + " where " + KEY_NAME + "='" + cursor.getString(1) + "'");
        }
        cursor.close();
    }

    public List<Message> getAllChats(String name) {
        List<Message> messageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_NAME + " = '" + name + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message contact = new Message(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),"");
                messageList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messageList;
    }
}
