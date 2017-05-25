package com.samirthebti.amen_go.utils;

/**
 * Amen_Go
 * Created by Samir Thebti on 5/17/17.
 * thebtisam@gmail.com
 */

import android.content.Context;


public class MunixUtilities {

    public static Context context;

    private MunixUtilities() {
        throw new UnsupportedOperationException( "Buuuuu" );
    }


    public static void init( Context context ) {
        MunixUtilities.context = context.getApplicationContext();
    }
}
