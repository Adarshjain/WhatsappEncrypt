package com.encrpyt.whatsapp.whatsappencrypt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Conversation extends AppCompatActivity implements View.OnClickListener {

    DBHelper db = new DBHelper(Conversation.this);
    MyReceiver myReceiver;
    private String Name, Number;
    private EditText Message;
    private LinearLayout ImageLayout;
    private ChatAdapter chatAdapter;
    public static final int PICK_IMAGE_FOR_ENC = 1;
    public static final int PICK_IMAGE_FOR_DEC = 2;



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
//        final SharedPreferences sharedPref = getSharedPreferences("Data",Context.MODE_PRIVATE);
//        final String Sender = sharedPref.getString("number", "4567");
//        Log.e("Conversation Class",Sender);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            Name = b.getString("name");
            Number = b.getString("number");
        } else {
            Toast.makeText(getApplicationContext(), "Name and number not received!", Toast.LENGTH_SHORT).show();
            finish();
        }
//        try {
        if (Number != null) {
            Number = Number.replaceAll(" ", "");
            Number = Number.replace("+", "");
            if (Number.length() < 11)
                Number = "91" + Number;
        }
//        } catch (NullPointerException npe) {
//            Toast.makeText(getApplicationContext(), "NPE", Toast.LENGTH_SHORT).show();
//        }

        db.clearCount(Number);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.appBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Name);
        }
        ImageLayout = (LinearLayout) findViewById(R.id.image_layout);

        Message = (EditText) findViewById(R.id.msg);
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.encryptx).setOnClickListener(this);
        findViewById(R.id.decryptx).setOnClickListener(this);
        findViewById(R.id.close_image_layout).setOnClickListener(this);

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
        Log.e("sd","df");
        switch (view.getId()) {
            case R.id.send:
                if (Objects.equals(Message.getText().toString(), "")) {
                    return;
                }
                try {
                    String text = Message.getText().toString();
                    Message.setText("");
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Data",Context.MODE_PRIVATE);
                    String Sender = sharedPref.getString("number",null);
                    if (Sender == null) {
                        Log.e("Null","Null");
                        return;
                    }
                    Log.e("Conversation Class",Sender);
                    String receiver = Number.length() <= 10 ? Number : Number.substring(Number.length() - 10);
                    Crypt crypt = new Crypt(Sender,receiver);
                    String text1 = crypt.encrypt(text);
//                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
//                    text = mahEncryptor.encode(text);


                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("dd.MM.yyyy.HH.mm.ss").format(new Date());
                    Message message = new Message(timeStamp, Name, text, "r", Number, "0");
                    db.addMessage(message);
                    addIndex(message, db);
                    chatAdapter.setMyMessages(db.getAllChats(Number));
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text1);
                    sendIntent.putExtra("jid", Number + "@s.whatsapp.net");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);

                } catch (Exception ex) {
                    Toast.makeText(Conversation.this, ex.toString(), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case R.id.encryptx:
                Log.e("skjdf","SDfdsf");
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE_FOR_ENC);
                break;
            case R.id.decryptx:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, PICK_IMAGE_FOR_DEC);
                break;
            case R.id.close_image_layout:
                ImageLayout.setVisibility(View.GONE);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Data",Context.MODE_PRIVATE);
        String Sender = sharedPref.getString("number",null);
        if (Sender == null) {
            Log.e("Null","Null");
            return;
        }
        Log.e("Conversation Class",Sender);
        String receiver = Number.length() <= 10 ? Number : Number.substring(Number.length() - 10);
        Crypt C = new Crypt(Sender,receiver);

        if (requestCode == PICK_IMAGE_FOR_ENC) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    InputStream iStream;
                    try {
                        FileMeta.FileMetaData fm = FileMeta.getFileMetaData(this,uri);
                        String tempFileName = fm.displayName;
//                        String extension = tempFileName[tempFileName.length - 1];
                        Log.e("filemeta",fm.toString());
                        iStream = getContentResolver().openInputStream(uri);
                        byte[] inputData = getBytes(iStream);
//                        Crypt C = new Crypt("8682084784","9486283531");
                        byte[] enc = C.fileEncrypt(inputData);
                        File file;
                        FileOutputStream outputStream;
                        try {
                            file = new File(Environment.getExternalStorageDirectory(), tempFileName + ".zeno");

                            outputStream = new FileOutputStream(file);
                            outputStream.write(enc);
                            outputStream.close();
                            Log.e("df","Adf");
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setAction(Intent.ACTION_SEND);
                            share.setType("application/pdf");
                            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            share.setPackage("com.whatsapp");

                            startActivity(Intent.createChooser(share, "Share Image"));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        new ImageSaver(this).setFileName("lol").setDirectoryName("img").save(bitmap);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(enc, 0, enc.length);
//                        ImageView imageView = (ImageView) findViewById(R.id.imageTest);
// Set the Bitmap data to the ImageView
//                        imageView.setImageBitmap(bitmap);
                        Log.e("this","done");
// Get the Root View of the layout
//                        ViewGroup layout = (ViewGroup) findViewById(R.id.myouter);
// Add the ImageView to the Layout
//                        layout.addView(imageView);
//                        ImageView image = (ImageView) findViewById(R.id.imageTest);
//
//                        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, image.getWidth(),
//                                image.getHeight(), false));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_IMAGE_FOR_DEC){
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    InputStream iStream;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
//                        FileMeta.FileMetaData fm = FileMeta.getFileMetaData(this,uri);
//                        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
//                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                        String x[] = fm.displayName.split(".");
//                        Log.e("filename",x[0]+ "." + x[1]);
//                        returnCursor.close();
                        byte[] inputData = getBytes(iStream);
                        byte[] enc = C.fileDecrypt(inputData);
                        Bitmap bmp = BitmapFactory.decodeByteArray(enc, 0, enc.length);
                        ImageView image = (ImageView) findViewById(R.id.decrypt_image);
//                        image.getLayoutParams().height = 450;
//                        image.getLayoutParams().width = MATCH_PARENT;


                        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, 400,
                                400, false));
                        ImageLayout.setVisibility(View.VISIBLE);
//                        File file;
//                        FileOutputStream outputStream;
//                        try {
//                            file = new File(Environment.getExternalStorageDirectory(), x[0]+ "." + x[1]);
//
//                            outputStream = new FileOutputStream(file);
//                            outputStream.write(enc);
//                            outputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        new ImageSaver(this).setFileName("lol").setDirectoryName("img").save(bitmap);
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(enc, 0, enc.length);
//                        ImageView imageView = (ImageView) findViewById(R.id.imageTest);
// Set the Bitmap data to the ImageView
//                        imageView.setImageBitmap(bitmap);
                        Log.e("this","done");
// Get the Root View of the layout
//                        ViewGroup layout = (ViewGroup) findViewById(R.id.myouter);
// Add the ImageView to the Layout
//                        layout.addView(imageView);
//                        ImageView image = (ImageView) findViewById(R.id.imageTest);
//
//                        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, image.getWidth(),
//                                image.getHeight(), false));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
