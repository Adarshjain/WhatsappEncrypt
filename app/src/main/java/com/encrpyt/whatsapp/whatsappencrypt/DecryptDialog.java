package com.encrpyt.whatsapp.whatsappencrypt;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobapphome.mahencryptorlib.MAHEncryptor;

public class DecryptDialog extends Dialog implements View.OnClickListener {
    public Dialog d;
    private Activity c;
    private EditText Text;
    private TextView Decrypted;

    public DecryptDialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.decrypt_dialog);
        findViewById(R.id.dec).setOnClickListener(this);
        findViewById(R.id.deca).setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        Text = (EditText) findViewById(R.id.enc_dec);
        Decrypted = (TextView) findViewById(R.id.decrypted);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dec:
                try {//Check if "Chat" contains encrypted text or not - if yes continue else return
                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
                    Decrypted.setText(mahEncryptor.decode(Text.getText().toString()));
                } catch (Exception e) {
                    Toast.makeText(c.getApplicationContext(), "Invalid text", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.deca:
                try {//Check if "Chat" contains encrypted text or not - if yes continue else return
                    MAHEncryptor mahEncryptor = MAHEncryptor.newInstance("This is sample SecretKeyPhrase");
                    Decrypted.setText(mahEncryptor.decode(Text.getText().toString()));
                } catch (Exception e) {
                    Toast.makeText(c.getApplicationContext(), "Invalid text", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.close:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
