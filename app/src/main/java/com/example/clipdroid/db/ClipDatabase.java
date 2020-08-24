package com.example.clipdroid.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

@Database(entities = {Clip.class}, version = 1, exportSchema = false)
public abstract class ClipDatabase extends RoomDatabase {
    private static ClipDatabase instance;
    public abstract ClipDao copyDao();

    public static synchronized ClipDatabase getInstance(Context context){
        if( instance == null ){
            instance = Room.databaseBuilder(context, ClipDatabase.class, "clip_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return  instance;
    }

    private static Callback roomCallback =  new Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateCopyAsyncTask(instance).execute();
        }
    };

    private static class PopulateCopyAsyncTask extends AsyncTask<Void, Void, Void>{
        private static ClipDao clipDao;
        private PopulateCopyAsyncTask(ClipDatabase database){
            clipDao = database.copyDao();
        }
        @Override
        protected Void doInBackground(Void... Void) {
            clipDao.insert(new Clip(Clip.TYPE_TEXT, "Clip 1", Clip.dateToTime( new Date()), Clip.MAIN_LIST));
            clipDao.insert(new Clip(Clip.TYPE_TEXT, "Clip 2", Clip.dateToTime( new Date()), Clip.MAIN_LIST));
            clipDao.insert(new Clip(Clip.TYPE_TEXT, "Clip 3", Clip.dateToTime( new Date()), Clip.MAIN_LIST));
            return null;
        }
    }
}
