package com.encrpyt.whatsapp.whatsappencrypt;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Objects;

public class DialerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String pwd = intent.getData().getHost();
        if (Objects.equals(pwd, "4556")) {
//            PackageManager p = context.getPackageManager();
//            ComponentName componentName = new ComponentName(context, PasswordActivity.class);
//            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            Intent i = new Intent(context, PasswordActivity.class);
            i.putExtra("data", pwd);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}