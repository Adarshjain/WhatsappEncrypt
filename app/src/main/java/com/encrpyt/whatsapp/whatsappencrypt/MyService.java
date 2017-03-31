package com.encrpyt.whatsapp.whatsappencrypt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.mobapphome.mahencryptorlib.MAHEncryptor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends NotificationListenerService {

    final static String CONVERSE = "converse";
    final static String INDEX = "index";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        DBHelper db = new DBHelper(getApplication());
        String pack = sbn.getPackageName();
        if (pack.equals("com.whatsapp")) {
            String ticker = (sbn.getNotification().tickerText != null) ? sbn.getNotification().tickerText.toString() : "";
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = (extras.getCharSequence("android.text") != null) ? "" + extras.getCharSequence("android.text") : "";
            if (!(title.contains("(") && title.contains("messages") && title.contains(")"))) {
                if (ticker.contains("Message from")) {
                    title = ticker.substring(13);
                }
                if (title.charAt(title.length() - 3) == ':') {
                    title = title.substring(0, title.length() - 3);
                }
                try {
                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
                    mahEncryptor.decode(text);

                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date());
                    String number = getPhoneNumber(title, getApplicationContext());
                    Message message = new Message(timeStamp, title, text, "l", number);
                    addMessage(message, db);
                    addIndex(message, db);
                } catch (Exception ignored) {
                    Log.e("err", ignored.toString());
                }
            }
        }
    }

    private void addMessage(Message message, DBHelper db) {
        db.addMessage(message);

        Intent intent = new Intent();
        intent.setAction(CONVERSE);
        sendBroadcast(intent);
    }

    private void addIndex(Message message, DBHelper db) {
        db.deleteIndexIfExists(message.getName());
        db.addIndex(message);

        Intent intent = new Intent();
        intent.setAction(INDEX);
        sendBroadcast(intent);
    }

    public String getPhoneNumber(String name, Context context) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if (ret == null)
            ret = name;
        return ret;
    }
}
