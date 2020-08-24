package com.example.clipdroid.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Clip {
    @Ignore
    public static final int TYPE_TEXT = 1, TYPE_IMAGE = 2;
    @Ignore
    public static final int MAIN_LIST = 0, TRASH_LIST = 1, FAV_LIST = 2, SEARCH_LIST = 3;
    @Ignore
    public static final boolean ASC = true, DESC = false;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    private int rowId;
    private int type;
    private String content;
    private String time;
    private int place;

    @Ignore
    private Date date;

    public Clip(int type, String content, String time, int place){
        this.type = type;
        this.content = content;
        this.date = new Date();
        this.time = time;
        this.place = place;
        this.time = getTime();
    }

    public int getRowId() {
        return rowId;
    }

    public int getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public int getPlace() {
        return place;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public String getTime(){
        return time;
    }

    public static String dateToTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ssa, E dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    @NonNull
    @Override
    public String toString() {
        return "type: "+type+", content: "+content+", time: "+ getTime()+", listName: "+getPlace();
    }
}
