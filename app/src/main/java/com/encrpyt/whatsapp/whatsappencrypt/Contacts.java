package com.encrpyt.whatsapp.whatsappencrypt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity {
    ListView li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.appBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        li = (ListView) findViewById(R.id.all_list);

        new LoadContactsAsync().execute();
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

    class LoadContactsAsync extends AsyncTask<Void, Void, ArrayList<String>> {

        ProgressDialog pd;
        Cursor c;
        ArrayList<String> cont = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Contacts.this);
            pd.setTitle("Loading Contacts");
            pd.setMessage("Please wait...");
            pd.show();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> contacts = new ArrayList<>();

            c = Contacts.this.getContentResolver().query(
                    ContactsContract.RawContacts.CONTENT_URI,
                    new String[]{ContactsContract.RawContacts._ID,
                            ContactsContract.RawContacts.CONTACT_ID,
                            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY},
                    ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                    new String[]{"com.whatsapp"},
                    ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " ASC");
            if (c != null) {
                if (c.getCount() > 0)
                    while (c.moveToNext()) {
                        contacts.add(c.getString(c.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY)));
                        cont.add(c.getString(c.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)));
                    }
                c.close();
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            super.onPostExecute(contacts);
            pd.dismiss();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    Contacts.this, R.layout.cont_list_item, contacts);
            li.setAdapter(adapter);
            li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String whatsappContactId = cont.get(i);
                    if (whatsappContactId != null) {
                        Cursor whatsAppContactCursor = Contacts.this.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{whatsappContactId},
                                null);
                        if (whatsAppContactCursor != null) {
                            whatsAppContactCursor.moveToFirst();
                            String id = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                            String name = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            whatsAppContactCursor.close();
                            Intent converse = new Intent(Contacts.this, Conversation.class);
                            converse.putExtra("id", id);
                            converse.putExtra("name", name);
                            converse.putExtra("number", number);
                            startActivity(converse);
                            Contacts.this.finish();
                        }
                    }
                }
            });
        }
    }
}
