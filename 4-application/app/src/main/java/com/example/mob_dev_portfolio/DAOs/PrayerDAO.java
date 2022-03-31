package com.example.mob_dev_portfolio.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mob_dev_portfolio.models.PrayerModel;

import java.util.List;

@Dao
public interface PrayerDAO {

    @Query("SELECT * FROM prayers")
    List<PrayerModel> getAllPrayers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPrayerModel(PrayerModel prayerModel);

    @Query("DELETE FROM prayers")
    void clearPrayerTable();
}
