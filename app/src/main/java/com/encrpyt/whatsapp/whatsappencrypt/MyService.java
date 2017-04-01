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

import com.encrpyt.whatsapp.whatsappencrypt.Adapter.IndexAdapter;
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
            String Name = extras.getString("android.text");
            String Chat = (extras.getCharSequence("android.text") != null) ? "" + extras.getCharSequence("android.text") : "";
            if (!(Name.contains("(") && Name.contains("messages") && Name.contains(")"))) {
                if (ticker.contains("Message from")) {
                    Chat = Name;
                    Name = ticker.substring(13);
                }
                try {
                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
                    mahEncryptor.decode(Chat);
                } catch (Exception e) {
                    return;
                }
                try {
                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
                    mahEncryptor.decode(Name);
                    return;
                } catch (Exception ignored) {
                }
                if (Name.charAt(Name.length() - 3) == ':') {
                    Name = Name.substring(0, Name.length() - 3);
                }
                try {
                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
                    mahEncryptor.decode(Chat);

                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date());
                    String number = getPhoneNumber(Name, getApplicationContext());
                    Message message = new Message(timeStamp, Name, Chat, "l", number);
                    addMessage(message, db);
                    addIndex(message, db);
                } catch (Exception ignored) {
                    Log.e("Service Err", ignored.toString());
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

        IndexAdapter indexAdapter = new IndexAdapter(getApplicationContext());
        indexAdapter.setIndex(db.getAllIndex());
        indexAdapter.notifyDataSetChanged();

        Intent intent = new Intent();
        intent.setAction(INDEX);
        sendBroadcast(intent);
    }

    public String getPhoneNumber(String name, Context conChat) {
        String ret = null;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = conChat.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
