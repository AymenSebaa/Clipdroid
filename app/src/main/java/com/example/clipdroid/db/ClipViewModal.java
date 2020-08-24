package com.example.clipdroid.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.clipdroid.ClipActivity;

import java.util.List;

public class ClipViewModal extends AndroidViewModel {
    private ClipRepository repository;

    public ClipViewModal(@NonNull Application application) {
        super(application);
        repository = new ClipRepository(application);
    }

    public void insert(Clip... clip){
        repository.insert(clip);
    }
    public void update(Clip... clip){
        repository.update(clip);
    }
    public void delete(Clip... clip){
        repository.delete(clip);
    }
    public void deleteAll(int place){
        repository.deleteAll(place);
    }
    public void moveClips(int currentPlace, int newPlace){
        repository.moveClips(currentPlace, newPlace);
    }
    public void getAll(){
        repository.getAll();
    }

    public LiveData<List<Clip>> getMainClips() {
        return repository.getMainClips();
    }
    public LiveData<List<Clip>> getTrashClips() {
        return repository.getTrashClips();
    }
    public LiveData<List<Clip>> getFavClips() {
        return repository.getFavClips();
    }
    public LiveData<List<Clip>> getSearchClips(String search) {
        return repository.getSearchClips(search);
    }

    public LiveData<Integer> getMainSize() {
        return repository.getMainSize();
    }
    public LiveData<Integer> getTrashSize() {
        return repository.getTrashSize();
    }
    public LiveData<Integer> getFavSize() {
        return repository.getFavSize();
    }
    public LiveData<Integer> getSearchSize(String search) {
        return repository.getSearchSize(search);
    }


}
