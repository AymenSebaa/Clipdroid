package com.example.clipdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SaveLoadPrefs {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final float DEFAULT_WINDOW_HEIGHT = .30f;
    public static final int DEFAULT_HEAD_Y = 200;

    public static float window_height = DEFAULT_WINDOW_HEIGHT;
    public static int head_y = DEFAULT_HEAD_Y;

    public static final String SHARED_KEY = "com.example.Clipdroid.PREFERENCE_FILE_KEY";
    public static final String WINDOW_HEIGHT = "WINDOW_HEIGHT";
    public static final String HEAD_Y = "HEAD_Y";

    public SaveLoadPrefs(Context context) {
         preferences = context.getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
         editor = preferences.edit();
    }

    public void saveHeadY(){
        editor.putInt(HEAD_Y, head_y);
        editor.apply();
        Log.d("SaveLoadPrefs", "saveHeadY "+head_y);
    }
    public void loadHeadY(){
        head_y = preferences.getInt(HEAD_Y, DEFAULT_HEAD_Y);
        Log.d("SaveLoadPrefs", " loadHeadY"+head_y);
    }

    public void saveWindowHeight(){
        editor.putFloat(WINDOW_HEIGHT, window_height);
        editor.apply();
        Log.d("SaveLoadPrefs", "saveWindowHeight "+window_height);
    }
    public void loadWindowHeight(){
        window_height = preferences.getFloat(WINDOW_HEIGHT, DEFAULT_WINDOW_HEIGHT);
        Log.d("SaveLoadPrefs", "loadWindowHeight"+window_height);
    }

}
