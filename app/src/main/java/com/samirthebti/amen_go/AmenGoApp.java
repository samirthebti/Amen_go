package com.samirthebti.amen_go;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

/**
 * Amen_Go
 * Created by Samir Thebti on 5/17/17.
 * thebtisam@gmail.com
 */

public class AmenGoApp extends Application {
    public static final String TAG = AmenGoApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Realm.init(this);
        // plase keep it :D
        Stetho.initializeWithDefaults(this);
    }
}
