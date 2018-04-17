package com.encrpyt.whatsapp.whatsappencrypt;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main2Activity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    public static final int PICK_IMAGE_D = 2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    InputStream iStream;
                    try {
                        FileMeta.FileMetaData fm = FileMeta.getFileMetaData(this,uri);
                        Log.e("filemeta",fm.toString());
                        iStream = getContentResolver().openInputStream(uri);
                        byte[] inputData = getBytes(iStream);
                        Crypt C = new Crypt("8682084784","9486283531");
                        byte[] enc = C.fileEncrypt(inputData);
                        File file;
                        FileOutputStream outputStream;
                        try {
                            file = new File(Environment.getExternalStorageDirectory(), "MyCache");

                            outputStream = new FileOutputStream(file);
                            outputStream.write(enc);
                            outputStream.close();
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
        if (requestCode == PICK_IMAGE_D){
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    InputStream iStream;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
                        Cursor returnCursor =
                                getContentResolver().query(uri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        String x = returnCursor.getString(nameIndex);
                        Log.e("filename",x);
                        returnCursor.close();
                        byte[] inputData = getBytes(iStream);
                        Crypt C = new Crypt("8682084784","9486283531");
                        byte[] enc = C.fileDecrypt(inputData);
                        File file;
                        FileOutputStream outputStream;
                        try {
                            file = new File(Environment.getExternalStorageDirectory(), "MyCache.jpg");

                            outputStream = new FileOutputStream(file);
                            outputStream.write(enc);
                            outputStream.close();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, PICK_IMAGE_D);
            }
        });
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
