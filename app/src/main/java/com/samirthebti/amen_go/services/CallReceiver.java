package com.samirthebti.amen_go.services;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.facebook.stetho.common.StringUtil;
import com.samirthebti.amen_go.Model.MyContact;
import com.samirthebti.amen_go.MyDialog;
import com.samirthebti.amen_go.utils.Preferences;

import java.lang.reflect.Method;
import java.util.Date;

import io.realm.Realm;

/**
 * Amen_Go
 * Created by Samir Thebti on 5/17/17.
 * thebtisam@gmail.com
 */

public class CallReceiver extends PhoneStateReceiver {
    public static final String TAG = CallReceiver.class.getSimpleName();

    private boolean autorised = false;
    private MyContact myContact;
    private Handler handler = new Handler();

    @Override
    protected void onIncomingCallStarted(Context ctx, final String number, Date start) {
        String val = Preferences.readSharedPreference(ctx, "maxSpeed", "");
        float maxSpeed = Float.valueOf(val);
        Intent intent = new Intent(ctx, MyDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Context context = ctx;
        final String nbr = StringUtil.removeAll(number, '+');
        Log.i(TAG, "caller number: " + nbr);
        Log.i(TAG, "onIncomingCallStarted: max speed  " + maxSpeed);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                myContact = realm.where(MyContact.class).equalTo("phone", nbr).findFirst();
            }
        });

        Log.e(TAG, "onIncomingCallStarted: " + myContact);
        if (myContact != null) {
            autorised = true;
            Log.d(TAG, "onIncomingCallStarted: "
                    + myContact.getPhone());
            Log.d(TAG, "onIncomingCallStarted: autaurised true");
        }
        realm.close();
        if (autorised) {
            Log.i(TAG, "onIncomingCallStarted: AUTAURISED true");
            return;
        }

        float speed = Preferences.readSharedPreference(ctx, "speed", 0.0f);
        Log.i(TAG, "onIncomingCallStarted: Speed   " + speed);
        if (speed == 0) {
            return;
        }
        if (maxSpeed == 0) {
            return;
        }

        if (speed > maxSpeed) {
            ctx.startActivity(intent);
            Log.d(TAG, "starting activity .......: ");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: ");
                    try {
                        declinePhone(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 20000);


        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
    }


    private void declinePhone(Context context) throws Exception {
        Log.e(TAG, "declinePhone: ");
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("unable", "msg cant dissconect call....");

        }
    }

}