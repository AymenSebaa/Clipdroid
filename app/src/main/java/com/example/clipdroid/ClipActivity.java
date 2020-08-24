package com.example.clipdroid;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clipdroid.db.Clip;
import com.example.clipdroid.db.ClipRepository;
import com.example.clipdroid.services.ClipService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class ClipActivity implements LifecycleOwner {
    public static final String TAG = "ClipActivity";

    public static boolean active = false;
    private WindowManager windowManager;
    public String search = "";
    View masterPanel;
    Context context;
    ClipRepository repository;
    RecyclerView recyclerView;
    Animation rotateClockwise, rotateAntiClockwise;
    DisplayMetrics metrics;
    WindowManager.LayoutParams paramsClip;
    ClipAdapter clipAdapter;

    LinearLayout divider;
    RelativeLayout relativeSearch;
    ImageView btnScreen;
    ImageView btnSearch, btnReturn;
    EditText inputSearch;

    List<ClipButton> clipButtons;

    SaveLoadPrefs saveLoadPrefs;
    MoreOptions moreOptions;
    float scale;

    LifecycleRegistry lifecycleRegistry;

    public ClipActivity(Context context, SaveLoadPrefs prefs, View masterPanel, ClipRepository repository){
        this.context = context;
        this.masterPanel = masterPanel;
        this.repository = repository;
        this.recyclerView = masterPanel.findViewById(R.id.clip_list);
        this.metrics = context.getResources().getDisplayMetrics();
        this.saveLoadPrefs = prefs;
        this.clipAdapter = new ClipAdapter();
        this.windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        this.btnSearch = masterPanel.findViewById(R.id.btn_search);
        this.btnReturn = masterPanel.findViewById(R.id.btn_return);
        this.inputSearch = masterPanel.findViewById(R.id.input_search);
        this.divider = masterPanel.findViewById(R.id.divider);
        this.relativeSearch = masterPanel.findViewById(R.id.relative_search);
        this.btnScreen = masterPanel.findViewById(R.id.btn_screen);

        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(0);
        clipAdapter.setContext(context);
        recyclerView.setAdapter(clipAdapter);

        scale = context.getResources().getDisplayMetrics().density;
        saveLoadPrefs.loadWindowHeight();
        ClipButton.currentList = ClipButton.lastList = 0;


        this.rotateClockwise = AnimationUtils.loadAnimation(context,R.anim.rotate_clockwise);
        this.rotateAntiClockwise = AnimationUtils.loadAnimation(context,R.anim.rotate_anti_clockwise);

        clipButtons = new ArrayList<>();
        clipButtons.add(new ClipButton(context, this, masterPanel, repository, clipAdapter,
                R.id.bdg_main_size, R.id.btn_main, R.id.bdg_main_order,
                clipButtons, Clip.MAIN_LIST, divider, R.drawable.round_corners_mint,
                R.drawable.round_corners_mint_border));
        clipButtons.add(new ClipButton(context, this, masterPanel, repository, clipAdapter,
                R.id.bdg_trash_size, R.id.btn_trash, R.id.bdg_trash_order,
                clipButtons, Clip.TRASH_LIST, divider, R.drawable.round_corners_red,
                R.drawable.round_corners_red_border));
        clipButtons.add(new ClipButton(context, this, masterPanel, repository, clipAdapter,
                R.id.bdg_fav_size, R.id.btn_fav, R.id.bdg_fav_order,
                clipButtons, Clip.FAV_LIST, divider, R.drawable.round_corners_yellow,
                R.drawable.round_corners_yellow_border));
        clipButtons.add(new ClipButton(context, this, masterPanel, repository, clipAdapter,
                R.id.bdg_search_size, R.id.btn_search, R.id.bdg_search_order,
                clipButtons, Clip.SEARCH_LIST, divider, R.drawable.round_corners_blue,
                R.drawable.round_corners_blue_border));
        clipButtons.get(Clip.MAIN_LIST).setSelected(true);

        this.moreOptions = new MoreOptions(context, repository, saveLoadPrefs, btnScreen, clipButtons);

        paramsClip = ClipService.paramsPanel;
        paramsClip.height = (int) (metrics.heightPixels * SaveLoadPrefs.window_height);
        paramsClip.y = ClipService.paramsHead.y + (paramsClip.height - ClipService.paramsHead.height)/2;

        btnScreen.setTag(R.drawable.ic_max);

        btnScreen.setOnClickListener(v -> {
            if((Integer) btnScreen.getTag() == R.drawable.ic_max){
                btnScreen.startAnimation(rotateClockwise);
                btnScreen.setBackgroundResource(R.drawable.round_corners_purple_border);
                btnScreen.setImageResource(R.drawable.ic_min);
                btnScreen.setTag(R.drawable.ic_min);
                ClipService.paramsPanel.height =  metrics.heightPixels;
                windowManager.updateViewLayout(masterPanel, ClipService.paramsPanel);
            } else {
                btnScreen.startAnimation(rotateAntiClockwise);
                btnScreen.setBackgroundResource(R.drawable.round_corners);
                btnScreen.setImageResource(R.drawable.ic_max);
                btnScreen.setTag(R.drawable.ic_max);
                ClipService.paramsPanel.height =  (int) (metrics.heightPixels*SaveLoadPrefs.window_height);
                windowManager.updateViewLayout(masterPanel, ClipService.paramsPanel);
            }
        });

        inputSearch.setOnFocusChangeListener((v, hasFocus) -> {
            Log.d(TAG, "inputSearch focused: "+v.hasFocus()+" "+v.isFocusable()+" "+v.isFocused());
            if(hasFocus){
                ClipService.paramsPanel.flags =  WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
            } else {
                ClipService.paramsPanel.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        |  WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM ;
            }
            windowManager.updateViewLayout(masterPanel, paramsClip);

        });
        inputSearch.setOnClickListener(v -> {
            Log.d(TAG, "inputSearch clicked "+v.hasFocus()+" "+v.isFocusable()+" "+v.isFocused());
                ClipService.paramsPanel.flags = WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR ;
                windowManager.updateViewLayout(masterPanel, paramsClip);
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                search = "%"+s+"%";
                ClipButton clipButton = clipButtons.get(Clip.SEARCH_LIST);
                clipButton.setClipList(repository.getSearchClips(search));
                clipButton.setClipSize(repository.getSearchSize(search));
                clipButton.refresh();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch (ClipButton.currentList){
                    case Clip.MAIN_LIST:
                        if(direction == ItemTouchHelper.LEFT){
                            moveItem(Clip.TRASH_LIST, viewHolder.getAdapterPosition(),"Clip Moved to Trash.");
                        } else if(direction == ItemTouchHelper.RIGHT) {
                            moveItem(Clip.FAV_LIST, viewHolder.getAdapterPosition(),"Clip Moved to Favorites.");
                        }
                        break;
                    case Clip.TRASH_LIST:
                        if(direction == ItemTouchHelper.LEFT){
                            // TODO show dialog to confirm deletion
                            repository.delete(clipAdapter.getClipAt(viewHolder.getAdapterPosition()));
                            Toast.makeText(context, "Clip Deleted", Toast.LENGTH_SHORT).show();
                        } else if(direction == ItemTouchHelper.RIGHT) {
                            moveItem(Clip.MAIN_LIST, viewHolder.getAdapterPosition(),"Clip restored.");
                        }
                        break;
                    case Clip.FAV_LIST:
                        if(direction == ItemTouchHelper.LEFT){
                            moveItem(Clip.TRASH_LIST, viewHolder.getAdapterPosition(),"Clip Moved to Trash.");
                        } else if(direction == ItemTouchHelper.RIGHT){
                            moveItem(Clip.MAIN_LIST, viewHolder.getAdapterPosition(),"Clip removed from Favorites.");
                        }
                        break;
                }
            }
            void moveItem(int place, int position, String message){
                Clip clip = clipAdapter.getClipAt(position);
                clip.setPlace(place);
                repository.update(clip);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

        }).attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        Log.d(TAG, "getLifecycle: ");
        return lifecycleRegistry;
    }
}
