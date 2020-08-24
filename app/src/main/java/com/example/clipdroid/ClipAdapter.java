package com.example.clipdroid;


import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clipdroid.db.Clip;
import com.example.clipdroid.services.ClipAccessService;

public class ClipAdapter extends ListAdapter<Clip, ClipAdapter.ClipHolder> {
    private static final DiffUtil.ItemCallback<Clip> DIFF_CALLBACK = new DiffUtil.ItemCallback<Clip>() {
        @Override
        public boolean areItemsTheSame(@NonNull Clip oldItem, @NonNull Clip newItem) {
                return oldItem.getRowId() == newItem.getRowId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Clip oldItem, @NonNull Clip newItem) {
            return oldItem.getType() == newItem.getType() &&
                    oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.getTime().equals(newItem.getTime()) &&
                    oldItem.getPlace() == newItem.getPlace();
        }
    };

    public ClipAdapter() {
        super(DIFF_CALLBACK);
    }

    class ClipHolder extends RecyclerView.ViewHolder{
        private TextView content;
        private ImageView icon;
        private TextView time;
        public ClipHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.clip_text);
            icon = itemView.findViewById(R.id.clip_icon);
            time = itemView.findViewById(R.id.clip_time);
        }
    }


    public Clip getClipAt(int position){
        return getItem(position);
    }

    @NonNull
    @Override
    public ClipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clip_layout, parent, false);
        return new ClipHolder(itemView);
    }

    private Context context;
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ClipHolder holder, int position) {
        Clip clip = getItem(position);
        holder.content.setText(clip.getContent());
        holder.time.setText(clip.getTime());
        switch (clip.getPlace()){
            case Clip.MAIN_LIST: holder.icon.setImageResource(R.drawable.ic_main); break;
            case Clip.TRASH_LIST: holder.icon.setImageResource(R.drawable.ic_trash); break;
            case Clip.FAV_LIST: holder.icon.setImageResource(R.drawable.ic_fav); break;
        }
        holder.itemView.setOnClickListener(v -> {
            if(ClipAccessService.listNodeInfo.size() == 0){
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Input not selected yet!")
                        .setPositiveButton("OK", null)
                        .create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alertDialog.show();
            } else {
                Log.d("ClipAdapter", "Past "+
                        ClipAccessService.listNodeInfo.get(ClipAccessService.listNodeInfo.size()-1));
                ClipAccessService.pastText(clip.getContent());
            }
        });
    }

}