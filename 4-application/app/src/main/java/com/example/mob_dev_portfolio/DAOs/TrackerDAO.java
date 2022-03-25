package com.example.mob_dev_portfolio.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mob_dev_portfolio.models.Tracker;

import java.util.List;

@Dao
public interface TrackerDAO {

    @Query("SELECT * FROM tracker")
    List<Tracker> getAllTracker();

    @Query("SELECT * FROM tracker WHERE date = :date LIMIT 1")
    Tracker getTrackerByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTracker(Tracker tracker);

    @Query("DELETE FROM tracker")
    void deleteAll();

    @Delete
    void delete(Tracker tracker);
}
