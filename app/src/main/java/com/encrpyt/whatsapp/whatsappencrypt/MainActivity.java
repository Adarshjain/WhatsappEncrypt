package com.encrpyt.whatsapp.whatsappencrypt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.encrpyt.whatsapp.whatsappencrypt.Adapter.IndexAdapter;

public class MainActivity extends AppCompatActivity {

    DBHelper db = new DBHelper(MainActivity.this);
    MyReceiver myReceiver;
    private IndexAdapter indexAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.appBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        RecyclerView indexRecyclerView = (RecyclerView) findViewById(R.id.index_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        indexRecyclerView.setLayoutManager(mLayoutManager);
        indexAdapter = new IndexAdapter(getApplicationContext());
        indexRecyclerView.setAdapter(indexAdapter);
        indexAdapter.setIndex(db.getAllIndex());
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Contacts.class));
            }
        });
        startService(new Intent(MainActivity.this, MyService.class));

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        indexAdapter.setIndex(db.getAllIndex());
        indexAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.decrypt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.decrypt:
                DecryptDialog d = new DecryptDialog(MainActivity.this);
                d.show();
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this,
                MyService.class);
        startService(intent);
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.INDEX);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (indexAdapter != null)
                indexAdapter.setIndex(db.getAllIndex());
        }
    }
}