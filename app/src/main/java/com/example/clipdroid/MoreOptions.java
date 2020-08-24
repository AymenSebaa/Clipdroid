package com.example.clipdroid;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.clipdroid.db.Clip;
import com.example.clipdroid.db.ClipRepository;
import com.example.clipdroid.services.ClipService;

import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class MoreOptions {
    private WindowManager windowManager;
    private Context context;
    private ClipRepository repository;
    private SaveLoadPrefs prefs;
    private List<ClipButton> clipButtons;
    private View master;
    private DisplayMetrics metrics;
    private ImageView btnScreen;
    private WindowManager.LayoutParams moreOptionsParams;
    private TextView title;
    private ImageView icon, close;
    private RelativeLayout currentRelative, relativeScreen, relativeList;
    private LinearLayout background;
    private int currentList;
    private View btnALlToTrash, btnAllToFav, btnAllToMain, btnAllForever;


    public MoreOptions(Context context, ClipRepository repository, SaveLoadPrefs prefs,
                       ImageView btnScreen, List<ClipButton> clipButtons) {
        this.context = context;
        this.repository = repository;
        this.prefs = prefs;
        this.btnScreen = btnScreen;
        this.clipButtons = clipButtons;

        this.windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        this.metrics = context.getResources().getDisplayMetrics();

        initMaster();
        initMoreScreenOptions();
        initMoreListOptions();

    }

    void initMaster(){
        this.moreOptionsParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                PixelFormat.TRANSLUCENT);
        moreOptionsParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            moreOptionsParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            moreOptionsParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        moreOptionsParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        moreOptionsParams.windowAnimations = R.style.Animation_Design_BottomSheetDialog;

        this.master = LayoutInflater.from(context).inflate(R.layout.more_options, null);
        this.background = master.findViewById(R.id.more_option_background);
        this.icon = master.findViewById(R.id.btn_more_options_icon);
        this.title = master.findViewById(R.id.more_options_title);
        this.close = master.findViewById(R.id.btn_more_options_close);

        close.setOnClickListener(v -> {
            showHidBtn(currentList, View.VISIBLE);
            windowManager.removeView(master);
            currentRelative.setVisibility(View.GONE);
        });
    }

    void initMoreScreenOptions(){
        this.relativeScreen = master.findViewById(R.id.relative_more_option_screen);
        NumberPicker picker = master.findViewById(R.id.num_window_height);
        Button btnWindowHeight25 = master.findViewById(R.id.btn_window_height_25);
        Button btnWindowHeight50 = master.findViewById(R.id.btn_window_height_50);
        Button btnWindowHeight75 = master.findViewById(R.id.btn_window_height_75);
        RelativeLayout btnHidePanel = master.findViewById(R.id.btn_hide_panel);
        picker.setMinValue(20);
        picker.setMaxValue(80);
        picker.setValue((int) (SaveLoadPrefs.window_height*100));

        picker.setOnScrollListener((view, scrollState) -> setSaveWindowHeight(picker, picker.getValue()));
        btnWindowHeight25.setOnClickListener(v -> setSaveWindowHeight(picker, 25));
        btnWindowHeight50.setOnClickListener(v -> setSaveWindowHeight(picker, 50));
        btnWindowHeight75.setOnClickListener(v -> setSaveWindowHeight(picker, 75));

        btnScreen.setOnLongClickListener(v -> {
            if( (Integer) btnScreen.getTag() == R.drawable.ic_min ){
                btnScreen.performClick();
            }
            showMoreOptions(relativeScreen, R.drawable.pop_bg_purple, R.drawable.ic_max, "Screen");
            return true;
        });
        clipButtons.get(Clip.TRASH_LIST).getBtnList().setOnLongClickListener(v -> {
            showMoreOptions(relativeList, R.drawable.pop_bg_red, R.drawable.ic_trash, "Trash", Clip.TRASH_LIST);
            return true;
        });
        clipButtons.get(Clip.FAV_LIST).getBtnList().setOnLongClickListener(v -> {
            showMoreOptions(relativeList, R.drawable.pop_bg_yellow, R.drawable.ic_fav, "Favorites", Clip.FAV_LIST);
            return true;
        });
        clipButtons.get(Clip.MAIN_LIST).getBtnList().setOnLongClickListener(v -> {
            showMoreOptions(relativeList, R.drawable.pop_bg_mint, R.drawable.ic_main, "Main", Clip.MAIN_LIST);
            return true;
        });

        btnHidePanel.setOnClickListener(v -> {
            ClipService.head.setVisibility(View.VISIBLE);
            ClipService.panel.setVisibility(View.GONE);
            close.performClick();
        });
    }
    void setSaveWindowHeight(NumberPicker picker, int scale){
        picker.setValue(scale);
        SaveLoadPrefs.window_height = (float) picker.getValue()/100;
        ClipService.paramsPanel.height = (int) (metrics.heightPixels*SaveLoadPrefs.window_height);
        windowManager.updateViewLayout(ClipService.panel, ClipService.paramsPanel);
        prefs.saveWindowHeight();
    }
    void initMoreListOptions(){
        this.relativeList = master.findViewById(R.id.relative_more_option_list);
        btnALlToTrash = master.findViewById(R.id.btn_move_all_to_trash);
        btnAllToFav = master.findViewById(R.id.btn_move_all_to_fav);
        btnAllToMain = master.findViewById(R.id.btn_move_all_to_main);
        btnAllForever = master.findViewById(R.id.btn_delete_all_forever);

        btnALlToTrash.setOnClickListener(v -> {
            moveTo(currentList, Clip.TRASH_LIST);
            close.performClick();
        });
        btnAllToFav.setOnClickListener(v -> {
            moveTo(currentList, Clip.FAV_LIST);
            close.performClick();
        });

        btnAllToMain.setOnClickListener(v -> {
            moveTo(currentList, Clip.MAIN_LIST);
            close.performClick();
        });
        btnAllForever.setOnClickListener(v -> {
            repository.deleteAll(currentList);
            close.performClick();
        });

    }

    void showMoreOptions(RelativeLayout relative, int color, int icon, String title){
        currentRelative = relative;
        background.setBackgroundResource(color);
        this.icon.setImageResource(icon);
        this.title.setText(title);
        currentRelative.setVisibility(View.VISIBLE);
        windowManager.addView(master, moreOptionsParams);
    }

    void showHidBtn(int list, int gone){
        switch (list){
            case Clip.TRASH_LIST:
                btnALlToTrash.setVisibility(gone);
                break;
            case Clip.FAV_LIST:
                btnAllToFav.setVisibility(gone);
                break;
            case Clip.MAIN_LIST:
                btnAllToMain.setVisibility(gone);
                break;
        }
    }

    void showMoreOptions(RelativeLayout relative, int color, int icon, String title, int list){
        currentList = list;
        showHidBtn(currentList, View.GONE);
        showMoreOptions(relative, color, icon, title);
    }

    void moveTo(int currentPlace, int newPlace){
        repository.moveClips(currentPlace, newPlace);
        close.performClick();
    }
}
