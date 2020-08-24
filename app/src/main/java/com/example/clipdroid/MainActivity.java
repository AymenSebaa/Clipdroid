package com.example.clipdroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.example.clipdroid.services.ClipService;

public class MainActivity extends Activity {
    private static final int OVERLAY_PERMISSION_CODE = 5463;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            if(!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+ getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
            } else {
                startService(new Intent(MainActivity.this, ClipService.class));
            }
        } else {
            startService(new Intent(MainActivity.this, ClipService.class));
        }

         // finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OVERLAY_PERMISSION_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Settings.canDrawOverlays(this)){
            startService(new Intent(MainActivity.this, ClipService.class));
        }
    }
}
