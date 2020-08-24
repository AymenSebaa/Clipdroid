package com.example.clipdroid.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.clipdroid.ClipActivity;

import java.util.List;

@Dao
public interface ClipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Clip... clips);

    @Update
    void update(Clip... clip);

    @Delete
    void delete(Clip... clip);

    @Query("DELETE FROM Clip WHERE place = :place")
    void deleteAll(int place);

    @Query("SELECT * FROM Clip WHERE place = "+Clip.MAIN_LIST+" ORDER BY rowid DESC")
    LiveData<List<Clip>> getMainClips();

    @Query("SELECT * FROM Clip WHERE place = "+Clip.TRASH_LIST+" ORDER BY rowid DESC")
    LiveData<List<Clip>> getTrashClips();

    @Query("SELECT * FROM Clip WHERE place = "+Clip.FAV_LIST+" ORDER BY rowid DESC")
    LiveData<List<Clip>> getFavClips();

    @Query("SELECT * FROM Clip WHERE content LIKE :search ORDER BY rowid DESC")
    LiveData<List<Clip>> searchClips(String search);


    @Query("SELECT COUNT(rowid) FROM Clip WHERE place = "+Clip.MAIN_LIST)
    LiveData<Integer> getMainSize();

    @Query("SELECT COUNT(rowid) FROM Clip WHERE place = "+Clip.TRASH_LIST)
    LiveData<Integer> getTrashSize();

    @Query("SELECT COUNT(rowid) FROM Clip WHERE place = "+Clip.FAV_LIST)
    LiveData<Integer> getFavSize();

    @Query("SELECT COUNT(rowid) FROM Clip WHERE content LIKE :search")
    LiveData<Integer> searchSize(String search);


    @Query("UPDATE Clip SET place = :newPlace WHERE place = :currentPlace")
    void moveClips(int currentPlace, int newPlace);

    @Query("SELECT * FROM Clip ")
    List<Clip> getAll();

}
