package com.samirthebti.amen_go.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class GpsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            Intent intent1 = new Intent(context, MyGpsDialog.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
