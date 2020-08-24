package com.example.clipdroid.db;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.clipdroid.ClipActivity;

import java.util.List;

public class ClipRepository {

    private ClipDao clipDao;

    public ClipRepository(Application application){
        clipDao = ClipDatabase.getInstance(application).copyDao();
    }

    public void insert(Clip... clip){
        new InsertAsyncTask(clipDao).execute(clip);
    }
    public void delete(Clip... clip){
        new DeleteAsyncTask(clipDao).execute(clip);
    }
    public void update(Clip... clip){
        new UpdateAsyncTask(clipDao).execute(clip);
    }
    public void deleteAll(int place){
        new DeleteAllAsyncTask(clipDao).execute(place);
    }
    public void moveClips(int currentPlace, int newPlace){
        new MoveClipsAsyncTask(clipDao).execute(currentPlace, newPlace);
    }

    public void getAll(){
        new GetAllAsyncTask(clipDao).execute();
    }

    public LiveData<List<Clip>> getMainClips() {
        return clipDao.getMainClips();
    }
    public LiveData<List<Clip>> getTrashClips() {
        return clipDao.getTrashClips();
    }
    public LiveData<List<Clip>> getFavClips() {
        return clipDao.getFavClips();
    }
    public LiveData<List<Clip>> getSearchClips(String search) {
        return clipDao.searchClips(search);
    }

    public LiveData<Integer> getMainSize() {
        return clipDao.getMainSize();
    }
    public LiveData<Integer> getTrashSize() {
        return clipDao.getTrashSize();
    }
    public LiveData<Integer> getFavSize() {
        return clipDao.getFavSize();
    }

    public LiveData<Integer> getSearchSize(String search) {
        return clipDao.searchSize(search);
    }

    private static class InsertAsyncTask extends AsyncTask<Clip, Void, Void>{
        private ClipDao clipDao;
        public InsertAsyncTask(ClipDao clipDao) {
            this.clipDao = clipDao;
        }
        @Override
        protected Void doInBackground(Clip... clip) {
            clipDao.insert(clip[0]);
            return null;
        }
    }
    private static class UpdateAsyncTask extends AsyncTask<Clip, Void, Void>{
        private ClipDao clipDao;
        public UpdateAsyncTask(ClipDao clipDao) {
            this.clipDao = clipDao;
        }
        @Override
        protected Void doInBackground(Clip... clip) {
            clipDao.update(clip[0]);
            return null;
        }
    }
    private static class DeleteAsyncTask extends AsyncTask<Clip, Void, Void>{
        private ClipDao clipDao;
        public DeleteAsyncTask(ClipDao clipDao) {
            this.clipDao = clipDao;
        }
        @Override
        protected Void doInBackground(Clip... clip) {
            clipDao.delete(clip[0]);
            return null;
        }
    }
    private static class DeleteAllAsyncTask extends AsyncTask<Integer, Void, Void>{
        private ClipDao clipDao;
        public DeleteAllAsyncTask(ClipDao clipDao) {
            this.clipDao = clipDao;
        }
        @Override
        protected Void doInBackground(Integer... integers) {
            clipDao.deleteAll(integers[0]);
            return null;
        }
    }
    private static class MoveClipsAsyncTask extends AsyncTask<Integer, Void, Void>{
        private ClipDao clipDao;
        public MoveClipsAsyncTask(ClipDao clipDao) {
            this.clipDao = clipDao;
        }
        @Override
        protected Void doInBackground(Integer... integers) {
            clipDao.moveClips(integers[0], integers[1]);
            return null;
        }
    }

    private static class GetAllAsyncTask extends AsyncTask<Void, Void, Void>{
        private ClipDao clipDao;
        public GetAllAsyncTask(ClipDao clipDao) {
            this.clipDao = clipDao;
        }
        @Override
        protected Void doInBackground(Void... Void) {
            Log.d("ClipActivity", "clips "+clipDao.getAll());

            return null;
        }
    }

}
