package com.encrpyt.whatsapp.whatsappencrypt;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.encrpyt.whatsapp.whatsappencrypt.Adapter.IndexAdapter;

public class MainActivity extends AppCompatActivity {

    DBHelper db = new DBHelper(MainActivity.this);
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
    protected void onStart() {
        Intent intent = new Intent(MainActivity.this,
                MyService.class);
        startService(intent);
        super.onStart();
    }
}