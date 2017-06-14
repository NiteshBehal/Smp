package com.simplified.text.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.simplified.text.android.BuildConfig;

import java.util.Set;

/**
 * Helper class for SharedPreferences, it has all the basic functionality, it can be modified according to use case.
 */
public class SharedPreferenceManager {

    public static final String OPEN_NOTIFICATION_FRAGMENT = "OPEN_NOTIFICATION_FRAGMENT";
    public static final String HIGHLIGHTER_COLOR = "HIGHLIGHTER_COLOR";
    private String sharedPrefsFile = BuildConfig.APPLICATION_ID;
    private Editor mEditor;
    private SharedPreferences mPreferences;
    private Context mContext;

    public static final String EMAIL = "EMAIL";
    public static final String PASSWORD = "PASSWORD";
    public static final String USER_ID = "USER_ID";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String NOTIFICATION_SAVED = "NOTIFICATION_SAVED";

    /*public static final String UIDKEY = "UIDKEY";
    public static final String STORE_KEY = "STORE_KEY";
    public static final String STORE_CITY = "StoreCityKey";
    public static final String STORE_PIN = "StorePinKey";
    public static final String NAME_KEY = "NAME_KEY";


    public static final String USER_ID_KEY = "UserId";
    public static final String EMAIL_KEY = "EMAIL_KEY";
    public static final String PWD_KEY = "PWD_KEY";
    public static final String PHONE_KEY = "PHONE_KEY";
    public static final String CART_UPDATION_TIME = "CART_UPDATION_TIME";
    public static final String DO_DELETE_CART = "DO_DELETE_CART";
    public static final String OPEN_NOTIFICATION_FRAGMENT = "OPEN_NOTIFICATION_FRAGMENT";
    public static final String OPEN_ORDER_FRAGMENT = "OPEN_ORDER_FRAGMENT";*/

    private SharedPreferenceManager() {
        /**
         * Private constructor
         */
    }

    /**
     * @param context  Current context.
     * @param fileName name of the file to use, passing null will use default name specified.
     */
    public SharedPreferenceManager(@NonNull Context context, @Nullable String fileName) {
        mContext = context;
        sharedPrefsFile = (fileName == null) ? BuildConfig.APPLICATION_ID : (BuildConfig.APPLICATION_ID + "." + fileName);
        initEditor();
    }

    /**
     * gets invoked internally from the constructor.
     */
    private void initEditor() {
        mPreferences = mContext.getSharedPreferences(sharedPrefsFile, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public SharedPreferences getPreferences() {
        return mPreferences;
    }

    public Editor getEditor() {
        return mEditor;
    }

    public void setBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public void setString(String key, String value) {
        mEditor.putString(key, value.trim());
        mEditor.commit();
    }

    public void setInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public void setFloat(String key, float value) {
        mEditor.putFloat(key, value);
        mEditor.commit();
    }

    public void setLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public void setStringSet(String key, Set<String> value) {
        mEditor.putStringSet(key, value);
        mEditor.commit();
    }

    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public void clearPreferences() {
        setBoolean(SharedPreferenceManager.NOTIFICATION_SAVED, false);
        setString(SharedPreferenceManager.EMAIL, "");
        setString(SharedPreferenceManager.PASSWORD, "");
        setString(SharedPreferenceManager.USER_ID, "");
//        mEditor.clear().commit();
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public Long getLong(String key, Long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return mPreferences.getStringSet(key, defValue);
    }

    public boolean contains(String key) {
        return mPreferences.contains(key);
    }

    public static void RemoveAll(Context con) {

        SharedPreferenceManager spm = new SharedPreferenceManager(con, null);
        spm.setString(EMAIL, "");
        spm.setString(PASSWORD, "");


    }
}
