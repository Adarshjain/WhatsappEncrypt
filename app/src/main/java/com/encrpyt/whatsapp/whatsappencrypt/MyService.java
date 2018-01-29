package com.encrpyt.whatsapp.whatsappencrypt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.encrpyt.whatsapp.whatsappencrypt.Adapter.IndexAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyService extends NotificationListenerService {

    final static String CONVERSE = "converse";
    final static String INDEX = "index";
    final static String TRIM_REGEX = "\\([0-9]+\\h(messages)\\)";
    final static String TAG = "MyService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        DBHelper db = new DBHelper(getApplication());
        String pack = sbn.getPackageName();
        if (pack.equals("com.whatsapp")) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
            String MyNumber = sharedPref.getString("number", "1234");
            Bundle extras = sbn.getNotification().extras;
            String Name = extras.getString("android.title");
            String Chat = extras.getString("android.text");
            String Key = sbn.getKey();
            String Number = getNumber(Key);
            if (Number.equals("null"))
                return;
            Log.e("Service", Key);
            Long PostTime = sbn.getPostTime();
            String decrypted;
            if (Name != null && Name.matches(TRIM_REGEX)) return;
            Name = trim(Name, TRIM_REGEX); //trimming extra "(2 messages)"
            if (Name.charAt(Name.length() - 2) == ':') {// Remove ":" from Name
                Name = Name.substring(0, Name.length() - 3);
            }
//            String Number = getPhoneNumber(Name, getApplicationContext()); //Get phone number from contact using Name
//            try {
//                Number = Number.replaceAll(" ", "");
//                Number = Number.replace("+", "");
//            } catch (NullPointerException npe) {
//                Toast.makeText(getApplicationContext(), "NPE", Toast.LENGTH_SHORT).show();
//            }
            try {//Check if "Chat" contains encrypted text or not - if yes continue else return
                Crypt crypt = new Crypt(Number, MyNumber);
                decrypted = crypt.decrypt(Chat + "\n");
                Log.e("Service decchat", decrypted);
//                MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
//                mahEncryptor.decode(Chat);
            } catch (Exception e) {
                Log.e("Service dec", "Error Chat" + e);
                return;
            }
            try {//Check if "Name" contains encrypted text or not - if yes return else continue
//                MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
//                mahEncryptor.decode(Name);
                Crypt crypt = new Crypt(Number, MyNumber);
                crypt.decrypt(Name);
                Log.e("Service decname", decrypted);
                return;
            } catch (Exception ignored) {
                Log.e("Service dec", "Error Name");
            }
            Log.e("Service dec", decrypted);
            if (!Objects.equals(Number, Name)) {
                if (Number.length() < 11) Number = "91" + Number;
                String Count = db.getCount(Number);
                try {
                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date(PostTime));
                    Message message = new Message(timeStamp, Name, decrypted, "l", Number, Count);
                    if (!db.isOld(message.getNumber(), message.getTime())) {
                        addMessage(message, db);
                        addIndex(message, db);
                    }
                } catch (Exception ignored) {
                    Log.e("Service Err", ignored.toString());
                }
            }
        }
    }

    private String getNumber(String key) {
        String num[] = key.split("@");
        return num[1].equals("s.whatsapp.net") ? num[0].substring(num[0].length() - 10) : "null";
    }

    private void addMessage(Message message, DBHelper db) {
        db.addMessage(message);

        Intent intent = new Intent();
        intent.setAction(CONVERSE);
        sendBroadcast(intent);
    }

    private void addIndex(Message message, DBHelper db) {
        db.deleteIndexIfExists(message.getNumber());
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

    public String trim(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        // Check all occurrences
        while (matcher.find()) {
            text = text.substring(0, matcher.start());
        }
        return text.trim();
    }
}
