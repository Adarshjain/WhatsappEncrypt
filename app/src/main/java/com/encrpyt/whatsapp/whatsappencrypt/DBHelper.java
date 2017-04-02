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
    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "chatManager";

    private static final String TABLE_NAME = "chatsAll";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_NAME = "name";
    private static final String KEY_CHAT = "chat";
    private static final String KEY_DIRECTION = "direc";
    private static final String KEY_NUMBER = "number";

    private static final String INDEX_TABLE_NAME = "indexing";
    private static final String TAG = "DB";
    private static final String KEY_COUNT = "count";


    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TIME + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_CHAT + " TEXT," + KEY_DIRECTION + " TEXT," + KEY_NUMBER + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
        String INDEX_TABLE = "CREATE TABLE " + INDEX_TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT," + KEY_CHAT + " TEXT," + KEY_TIME + " TEXT," + KEY_NUMBER + " TEXT," + KEY_COUNT + " TEXT)";
        db.execSQL(INDEX_TABLE);
        Log.e(TAG, "Db created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + INDEX_TABLE_NAME);
        Log.e(TAG, "Db destroyed");
        onCreate(db);
    }

    void addMessage(Message message) {
        if (message.getName().contains("@") || message.getName().contains(":")) {
            Log.e(TAG, "rejected");
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, message.getTime());
        values.put(KEY_NAME, message.getName());
        values.put(KEY_CHAT, message.getChat());
        values.put(KEY_DIRECTION, message.getDirection());
        values.put(KEY_NUMBER, message.getNumber());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    void addIndex(Message message) {
        if (message.getName().contains("@") || message.getName().contains(":")) {//rejects group texts
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, message.getName());
        values.put(KEY_CHAT, message.getChat());
        values.put(KEY_TIME, message.getTime());
        values.put(KEY_NUMBER, message.getNumber());
        values.put(KEY_COUNT, message.getCount());

        db.insert(INDEX_TABLE_NAME, null, values);
        db.close();
    }

    List<Message> getAllIndex() {
        List<Message> indexList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + INDEX_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message contact = new Message(cursor.getString(3), cursor.getString(1), cursor.getString(2), "", cursor.getString(4), cursor.getString(5));
                indexList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return indexList;
    }

    void deleteIndexIfExists(String number) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(INDEX_TABLE_NAME, new String[]{KEY_ID, KEY_NUMBER}, KEY_NUMBER + "=?",
                new String[]{number}, null, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            db.execSQL("delete from " + INDEX_TABLE_NAME + " where " + KEY_NUMBER + "='" + cursor.getString(1) + "'");
        }
        cursor.close();
    }

    List<Message> getAllChats(String number) {
        List<Message> messageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_NUMBER + " = '" + number + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Message contact = new Message(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), "");
                messageList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messageList;
    }

    boolean isOld(String number, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(INDEX_TABLE_NAME,
                new String[]{KEY_ID}, KEY_NUMBER + "=? AND " + KEY_TIME + "=?",
                new String[]{number, time},
                null, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    String getCount(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(INDEX_TABLE_NAME, new String[]{KEY_COUNT}, KEY_NUMBER + "=?",
                new String[]{number}, null, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            int count = Integer.parseInt(cursor.getString(0)) + 1;
            cursor.close();
            return "" + count;
        }
        cursor.close();
        return "0";
    }

    void clearCount(String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_COUNT, "0");

        String[] args = new String[]{number};
        db.update(INDEX_TABLE_NAME, newValues, KEY_NUMBER + "=?", args);
    }
}
