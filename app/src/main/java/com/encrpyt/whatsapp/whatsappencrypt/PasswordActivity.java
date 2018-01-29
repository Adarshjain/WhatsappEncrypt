package com.encrpyt.whatsapp.whatsappencrypt;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class PasswordActivity extends AppCompatActivity {

    EditText p1, p2, p3, p4, Number;
    TextView Perm;
    Button saveNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final LinearLayout NumberPrompt = (LinearLayout) findViewById(R.id.numberPrompt);

        final SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Data",Context.MODE_PRIVATE);
        final String number = sharedPref.getString("number", "1234");
        Log.e("Password",number);
        if (!Objects.equals(number, "1234")) NumberPrompt.setVisibility(View.GONE);
//        PackageManager p = getPackageManager();
//        ComponentName componentName = new ComponentName(this, PasswordActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
//        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        Perm = (TextView) findViewById(R.id.perm);
        Number = (EditText) findViewById(R.id.number);
        saveNumber = (Button) findViewById(R.id.saveNumber);
        Perm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
                if (!getNotified()) {
                    Toast.makeText(getApplicationContext(), "Please grant Notification Access", Toast.LENGTH_LONG).show();
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            }
        });

        saveNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = Number.getText().toString();
                if (Objects.equals(num, "")) {
                    Toast.makeText(getApplicationContext(), "Please specify your current WhatsApp number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (num.length() != 10) return;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("number", num);
                editor.apply();
                NumberPrompt.setVisibility(View.GONE);

            }
        });
        Perm.setVisibility(View.VISIBLE);

        if (!getPermission()) {
            Toast.makeText(getApplicationContext(), "Please Grant Permission", Toast.LENGTH_SHORT).show();
        }
        p1 = (EditText) findViewById(R.id.pass1);
        p2 = (EditText) findViewById(R.id.pass2);
        p3 = (EditText) findViewById(R.id.pass3);
        p4 = (EditText) findViewById(R.id.pass4);

        p1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1)
                    p2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        p2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1)
                    p3.requestFocus();
                if (charSequence.length() == 0)
                    p1.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        p3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1)
                    p4.requestFocus();
                if (charSequence.length() == 0)
                    p2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        p4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    p3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getPermission()) {
                    return;
                } else if (NumberPrompt.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getApplicationContext(), "Please specify your current WhatsApp number!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!getNotified()) {
                        Toast.makeText(getApplicationContext(), "Please grant Notification Access", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        return;
                    }
                }
                String pt1, pt2, pt3, pt4;
                pt1 = p1.getText().toString();
                pt2 = p2.getText().toString();
                pt3 = p3.getText().toString();
                pt4 = p4.getText().toString();
                String finalPass = "" + pt1 + pt2 + pt3 + pt4;
                if (finalPass.equals("4556")) {
                    startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                    PasswordActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED)
                return true;
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                return false;
            }
        } else return true;
    }

    public boolean getNotified() {
        ComponentName cn = new ComponentName(getApplicationContext(), MyService.class);
        String flat = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!getPermission()) {
            Toast.makeText(getApplicationContext(), "Please Grant Permission", Toast.LENGTH_SHORT).show();
        } else {
            if (!getNotified()) {
                Toast.makeText(getApplicationContext(), "Please grant Notification Access", Toast.LENGTH_SHORT).show();
            } else {
                Perm.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!getNotified()) {
                        Toast.makeText(getApplicationContext(), "Please grant Notification Access", Toast.LENGTH_LONG).show();
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        return;
                    } else {
                        Perm.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    Perm.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
