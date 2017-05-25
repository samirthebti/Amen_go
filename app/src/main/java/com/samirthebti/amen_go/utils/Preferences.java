package com.samirthebti.amen_go.utils;

/**
 * Amen_Go
 * Created by Samir Thebti on 5/17/17.
 * thebtisam@gmail.com
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;


public class Preferences {

    public static SharedPreferences getSharedPreferenceManager(Context context) {
        try {
            return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        } catch (Exception e) {
            return null;
        }
    }

    public static SharedPreferences getSharedPreferenceManager() {
        return getSharedPreferenceManager(MunixUtilities.context);
    }

    /**
     * Elimina una preferencia almacenada en SharedPreferences
     *
     * @param context
     * @param key
     */
    public static void deleteSharedPreference(Context context, String key) {
        try {
            getSharedPreferenceManager(context).edit().remove(key).apply();
        } catch (Exception e) {
        }
    }

    public static void deleteSharedPreference(String key) {
        deleteSharedPreference(MunixUtilities.context, key);
    }

    /**
     * Elimina todas las Shared Preferences que empiecen con keyStartWith
     *
     * @param context
     * @param keyStartWith
     * @return
     */
    public static void deleteSharedPreferenceByPartialKey(Context context, String keyStartWith) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            Map<String, ?> keys = settings.getAll();
            SharedPreferences.Editor editor = settings.edit();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                if (entry.getKey().startsWith(keyStartWith)) {
                    editor.remove(entry.getKey());
                }
            }
            editor.apply();
        } catch (Exception e) {
        }
    }

    public static void deleteSharedPreferenceByPartialKey(String keyStartWith) {
        deleteSharedPreferenceByPartialKey(MunixUtilities.context, keyStartWith);
    }

    /**
     * Guardar una preferencia
     *
     * @param context
     * @param key
     * @param value
     */
    public static void writeSharedPreference(Context context, String key, String value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
        }
    }

    public static void writeSharedPreference(String key, String value) {
        writeSharedPreference(MunixUtilities.context, key, value);
    }

    /**
     * Guardar una preferencia
     *
     * @param context
     * @param key
     * @param value
     */
    public static void writeSharedPreference(Context context, String key, Boolean value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception e) {
        }
    }

    public static void writeSharedPreference(String key, Boolean value) {
        writeSharedPreference(MunixUtilities.context, key, value);
    }

    /**
     * Guardar una preferencia
     *
     * @param context
     * @param key
     * @param value
     */
    public static void writeSharedPreference(Context context, String key, long value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(key, value);
            editor.apply();
        } catch (Exception e) {
        }
    }

    public static void writeSharedPreference(String key, long value) {
        writeSharedPreference(MunixUtilities.context, key, value);
    }

    public static void writeSharedPreference(Context context, String key, int value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(key, value);
            editor.apply();
        } catch (Exception e) {
        }
    }

    public static void writeSharedPreference(String key, int value) {
        writeSharedPreference(MunixUtilities.context, key, value);
    }

    public static void writeSharedPreference(Context context, String key, float value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(key, value);
            editor.apply();
        } catch (Exception e) {
        }
    }

    public static void writeSharedPreference(String key, float value) {
        writeSharedPreference(MunixUtilities.context, key, value);
    }


    /**
     * Lee una preferencia
     *
     * @param context
     * @param key
     * @param default_value
     * @return
     */
    public static Boolean readSharedPreference(Context context, String key, Boolean default_value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            return settings.getBoolean(key, default_value);
        } catch (Exception e) {
            return default_value;
        }
    }

    public static Boolean readSharedPreference(String key, Boolean default_value) {
        return readSharedPreference(MunixUtilities.context, key, default_value);
    }

    /**
     * Lee una preferencia
     *
     * @param context
     * @param key
     * @param default_value
     * @return
     */
    public static long readSharedPreference(Context context, String key, long default_value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            return settings.getLong(key, default_value);
        } catch (Exception e) {
            return default_value;
        }
    }

    public static long readSharedPreference(String key, long default_value) {
        return readSharedPreference(MunixUtilities.context, key, default_value);
    }

    public static int readSharedPreference(Context context, String key, int default_value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            return settings.getInt(key, default_value);
        } catch (Exception e) {
            return default_value;
        }
    }

    public static int readSharedPreference(String key, int default_value) {
        return readSharedPreference(MunixUtilities.context, key, default_value);
    }

    public static float readSharedPreference(Context context, String key, float default_value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            return settings.getFloat(key, default_value);
        } catch (Exception e) {
            return default_value;
        }
    }

    public static float readSharedPreference(String key, float default_value) {
        return readSharedPreference(MunixUtilities.context, key, default_value);
    }

    /**
     * Lee una preferencia
     *
     * @param context
     * @param key
     * @param default_value
     * @return
     */
    public static String readSharedPreference(Context context, String key, String default_value) {
        try {
            SharedPreferences settings = getSharedPreferenceManager(context);
            return settings.getString(key, default_value);
        } catch (Exception e) {
            return default_value;
        }
    }

    public static String readSharedPreference(String key, String default_value) {
        return readSharedPreference(MunixUtilities.context, key, default_value);
    }
}
