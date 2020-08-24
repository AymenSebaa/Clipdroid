package com.example.clipdroid;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.clipdroid.db.Clip;
import com.example.clipdroid.db.ClipRepository;

import java.util.Collections;
import java.util.List;

public class ClipButton {
    public static final String TAG = "ClipButton";
    public static int lastList, currentList;
    private Context context;
    private View masterPanel;
    private LifecycleOwner lifecycle;
    private ClipRepository repository;
    private ClipAdapter clipAdapter;
    private LiveData<List<Clip>> clipList;
    private LiveData<Integer> clipSize;
    private TextView bdgSize;
    private ImageView btnList;
    private ImageView bdgOrder;
    private List<ClipButton> clipButtons;
    private int tagSize, tagList, tagOrder;
    private LinearLayout divider;
    private int roundColor, borderColor;
    private int listId;
    private boolean isSelected, isAsc;

    public ClipButton(Context context, LifecycleOwner lifecycle, View masterPanel, ClipRepository repository, ClipAdapter clipAdapter,
                      int tagSize, int tagList, int tagOrder, List<ClipButton> clipButtons,
                      int listId, LinearLayout divider, int roundColor, int borderColor) {
        this.context = context;
        this.lifecycle = lifecycle;
        this.masterPanel = masterPanel;
        this.repository = repository;
        this.clipAdapter = clipAdapter;
        this.tagSize = tagSize;
        this.tagList = tagList;
        this.tagOrder = tagOrder;
        this.clipButtons = clipButtons;
        this.listId = listId;
        this.divider = divider;
        this.roundColor = roundColor;
        this.borderColor = borderColor;

        initViews();
        setClick();

    }

    void initViews(){
        bdgSize = masterPanel.findViewById(tagSize);
        btnList = masterPanel.findViewById(tagList);
        bdgOrder = masterPanel.findViewById(tagOrder);
        bdgOrder.setTag(R.drawable.ic_down);

        setClipList();
        clipSize.observe(lifecycle, integer -> {
            if(integer > 0){ this.bdgSize.setVisibility(View.VISIBLE);}
            else { this.bdgSize.setVisibility(View.GONE); }
            this.bdgSize.setText(String.valueOf(integer));
        });
    }

    void clearLastClipButton(ClipButton clipButton){
        clipButton.btnList.setBackgroundResource(R.drawable.round_corners);
        clipButton.bdgOrder.setVisibility(View.GONE);
        clipButton.clipList.removeObservers(lifecycle);
        clipButton.isSelected = false;
    }

    void setClick(){
        btnList.setOnClickListener(v -> {
            if(listId == Clip.SEARCH_LIST){
                setClickSearch();
            }
            int tempList = lastList;
            lastList = currentList;
            currentList = listId;
            if(lastList == currentList){
                lastList = tempList;
            }
            Toast.makeText(context, "last: "+lastList+", curr: "+currentList, Toast.LENGTH_SHORT).show();
            if(lastList != listId){
                clearLastClipButton(clipButtons.get(lastList));
                bdgOrder.setVisibility(View.VISIBLE);
                btnList.setBackgroundResource(borderColor);
                divider.setBackgroundResource(roundColor);
                if(listId == Clip.SEARCH_LIST){
                    RelativeLayout relativeInputSearch = masterPanel.findViewById(R.id.relative_input_search);
                    relativeInputSearch.setVisibility(View.VISIBLE);
                }
            }

            if(isSelected){
                if(isAsc = !isAsc){
                    bdgOrder.setImageResource(R.drawable.ic_up);
                    bdgOrder.setTag(R.drawable.ic_up);
                } else {
                    bdgOrder.setImageResource(R.drawable.ic_down);
                    bdgOrder.setTag(R.drawable.ic_down);
                }
                List<Clip> list = clipList.getValue();
                Log.d("ClipButton", "list "+list);
                Collections.reverse(list);
                clipAdapter.submitList(list);
                clipAdapter.notifyDataSetChanged();
            }
            setSelected(true);
        });
    }

    void setClickSearch(){
        ImageView btnReturn = masterPanel.findViewById(R.id.btn_return);
        RelativeLayout relativeInputSearch = masterPanel.findViewById(R.id.relative_input_search);
        btnReturn.setOnClickListener(v -> {
            relativeInputSearch.setVisibility(View.GONE);
            clipButtons.get(lastList).btnList.performClick();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //    imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        });
    }

    void setClipList(){
        switch (listId){
            case Clip.MAIN_LIST:
                clipList = repository.getMainClips();
                clipSize = repository.getMainSize();
                break;
            case Clip.TRASH_LIST:
                clipList = repository.getTrashClips();
                clipSize = repository.getTrashSize();
                break;
            case Clip.FAV_LIST:
                clipList = repository.getFavClips();
                clipSize = repository.getFavSize();
                break;
            case Clip.SEARCH_LIST:
                clipList = repository.getSearchClips("");
                clipSize = repository.getSearchSize("");
                break;
        }
        clipList.observe(lifecycle, clips ->{
            Log.d(TAG, "setClipList: "+clips);
        });
    }

    public void setClipList(LiveData<List<Clip>> clipList) {
        this.clipList = clipList;
    }

    public void setClipSize(LiveData<Integer> clipSize) {
        this.clipSize = clipSize;
    }

    public void refresh(){
        clipList.observe(lifecycle, clipAdapter::submitList);
        clipSize.observe(lifecycle, integer -> {
            if(integer > 0){ this.bdgSize.setVisibility(View.VISIBLE);}
            else { this.bdgSize.setVisibility(View.GONE); }
            this.bdgSize.setText(String.valueOf(integer));
        });
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
        clipList.observe(lifecycle, clips -> {
            Log.d(TAG, "setSelected: "+clips);
            clipAdapter.submitList(clips);
            clipAdapter.notifyDataSetChanged();
        });
    }


    public ImageView getBtnList() {
        return btnList;
    }

    // TODO onLongPress Filter Search

}
