package com.example.clipdroid.services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.clipdroid.ClipActivity;
import com.example.clipdroid.R;
import com.example.clipdroid.SaveLoadPrefs;
import com.example.clipdroid.db.Clip;
import com.example.clipdroid.db.ClipRepository;

import java.util.Date;

public class ClipService extends Service {

    public static View head, panel;
    public static WindowManager.LayoutParams paramsHead, paramsFHead, paramsPanel;
    ClipAccessService accessService;
    WindowManager windowManager;
    GestureDetector gestureDetector;
    SaveLoadPrefs saveLoadPrefs;
    Animation rotateClockwise, rotateAntiClockwise;
    int initialYHead, initialTouchYHead;
    float scale;
    private ClipRepository repository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.saveLoadPrefs = new SaveLoadPrefs(this);
        this.scale = getResources().getDisplayMetrics().density;
        this.gestureDetector = new GestureDetector(this, new GestureListener());
        this.rotateClockwise = AnimationUtils.loadAnimation(this,R.anim.rotate_clockwise);
        this.rotateAntiClockwise = AnimationUtils.loadAnimation(this,R.anim.rotate_anti_clockwise);
        this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        this.repository = new ClipRepository(this.getApplication());


        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(intent);
        this.accessService = new ClipAccessService();

        saveLoadPrefs.loadHeadY();

        initHead();
        initPanel();
    }

    @SuppressLint("ClickableViewAccessibility")
    void initHead(){
        this.paramsHead = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                PixelFormat.TRANSLUCENT);

        paramsHead.gravity = Gravity.RIGHT;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            paramsHead.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            paramsHead.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        paramsHead.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        paramsHead.width = (int) (52 * scale);
        paramsHead.height = (int) (52 * scale);
        paramsHead.y = SaveLoadPrefs.head_y;
        this.head = LayoutInflater.from(this).inflate(R.layout.ic_clip, null);
        windowManager.addView(head, paramsHead);

        head.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    paramsFHead = paramsHead;
                    initialYHead = paramsFHead.y;
                    initialTouchYHead = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    SaveLoadPrefs.head_y = paramsFHead.y;
                    saveLoadPrefs.saveHeadY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    paramsFHead.y = initialYHead + (int) (event.getRawY() - initialTouchYHead);
                    windowManager.updateViewLayout(head, paramsFHead);
                    break;
            }

            return false;
        });
        ImageView imageClip = head.findViewById(R.id.ic_round_clip);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.addPrimaryClipChangedListener(() ->{
                Log.d("Clipboard", "getPrimaryClip index 0 "+clipboard.getPrimaryClip().getItemAt(0).getText());
                repository.insert(new Clip(Clip.TYPE_TEXT,
                        ""+clipboard.getPrimaryClip().getItemAt(0).getText(),
                        Clip.dateToTime(new Date()), Clip.MAIN_LIST));
                imageClip.startAnimation(rotateClockwise);
            });
        }
    }

    void initPanel(){
        paramsPanel = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                PixelFormat.TRANSLUCENT);

        paramsPanel.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            paramsPanel.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            paramsPanel.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        paramsPanel.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        paramsPanel.height = (int) (300 * scale);
        paramsPanel.y = SaveLoadPrefs.head_y;
        this.panel = LayoutInflater.from(this).inflate(R.layout.activity_clip, null);
        ClipActivity clipActivity = new ClipActivity(this, saveLoadPrefs, panel, repository);
        panel.setVisibility(View.GONE);
        windowManager.addView(panel, paramsPanel);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(head.getVisibility() == View.VISIBLE){
                head.setVisibility(View.GONE);
                panel.setVisibility(View.VISIBLE);
            } else {
                head.setVisibility(View.VISIBLE);
                panel.setVisibility(View.GONE);
            }
            Toast.makeText(ClipService.this, "One Tap", Toast.LENGTH_SHORT).show();
            return true;
        }
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Toast.makeText(ClipService.this, "Double Tap", Toast.LENGTH_SHORT).show();
            if(paramsHead.width == (int)(52 * scale) ){
                paramsHead.width = (int) (28 * scale);
                paramsHead.height = (int) (28 * scale);
            } else {
                paramsHead.width = (int)(52 * scale);
                paramsHead.height = (int) (52 * scale);
            }
            windowManager.updateViewLayout(head, paramsHead);
            return false;
        }
        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(ClipService.this, "Long Tap", Toast.LENGTH_SHORT).show();
            AlertDialog alertDialog = new AlertDialog.Builder(ClipService.this)
                    .setTitle("Quit Application?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        stopSelf();
                        System.exit(0);
                    })
                    .create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

}
