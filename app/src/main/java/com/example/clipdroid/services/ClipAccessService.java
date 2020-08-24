package com.example.clipdroid.services;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class ClipAccessService extends AccessibilityService {
    public static final String TAG = "ClipAccessService";
    public static List<AccessibilityNodeInfo> listNodeInfo=new ArrayList<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Log.d(TAG, "all "+event.toString());
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                AccessibilityNodeInfo nodeInfo = event.getSource();
                Log.d(TAG, "nodeInfo "+nodeInfo);
                if (nodeInfo != null) {
                    nodeInfo.refresh();
                    if(nodeInfo.getClassName().toString().contains("EditText")){
                        listNodeInfo.removeAll(listNodeInfo);
                        listNodeInfo.add(nodeInfo);
                        Log.d("ClipAccessService", "EditText "+ listNodeInfo.size());
                    }
                }
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    public static void pastText(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = new Bundle();
            AccessibilityNodeInfo nodeInfoSafe = listNodeInfo.get(listNodeInfo.size()-1);
            nodeInfoSafe.refresh();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bundle.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        nodeInfoSafe.getText().toString().replace(nodeInfoSafe.getHintText().toString(),"")+text);
            } else {
                bundle.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        nodeInfoSafe.getText().toString()+text);
            }
            nodeInfoSafe.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
        }
    }
}