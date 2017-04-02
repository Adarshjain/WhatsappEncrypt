package com.encrpyt.whatsapp.whatsappencrypt;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobapphome.mahencryptorlib.MAHEncryptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Conversation extends AppCompatActivity implements View.OnClickListener {

    DBHelper db = new DBHelper(Conversation.this);
    MyReceiver myReceiver;
    private String Name, Number;
    private EditText Message;
    private ChatAdapter chatAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.CONVERSE);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Bundle b = getIntent().getExtras();
        Name = b.getString("name");
        Number = b.getString("number");
        try {
            Number = Number.replaceAll(" ", "");
            Number = Number.replace("+", "");
        } catch (NullPointerException npe) {
            Toast.makeText(getApplicationContext(), "NPE", Toast.LENGTH_SHORT).show();
        }
        if (Number.length() < 11)
            Number = "91" + Number;

        db.clearCount(Number);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.appBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Name);
        }

        Message = (EditText) findViewById(R.id.msg);
        findViewById(R.id.send).setOnClickListener(this);

        RecyclerView chatsRecycler = (RecyclerView) findViewById(R.id.chats_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(true);
        chatsRecycler.setLayoutManager(mLayoutManager);
        chatAdapter = new ChatAdapter(getApplicationContext());
        chatsRecycler.setAdapter(chatAdapter);
        chatAdapter.setMyMessages(db.getAllChats(Number));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (Objects.equals(Message.getText().toString(), "")) {
                    return;
                }
                try {
                    String text = Message.getText().toString();
                    Message.setText("");
                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
                    text = mahEncryptor.encode(text);


                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("dd.MM.yyyy.HH.mm.ss").format(new Date());
                    Message message = new Message(timeStamp, Name, text, "r", Number,"0");
                    db.addMessage(message);
                    addIndex(message, db);
                    chatAdapter.setMyMessages(db.getAllChats(Number));
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    sendIntent.putExtra("jid", Number + "@s.whatsapp.net");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);

                } catch (Exception ex) {
                    Toast.makeText(Conversation.this, ex.toString(), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }

    private void addIndex(Message message, DBHelper db) {
        db.deleteIndexIfExists(message.getNumber());
        db.addIndex(message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (chatAdapter != null)
                chatAdapter.setMyMessages(db.getAllChats(Number));
            db.clearCount(Number);
        }
    }
}
