package com.example.mob_dev_portfolio.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mob_dev_portfolio.DAOs.PrayerDAO;
import com.example.mob_dev_portfolio.models.PrayerModel;

@Database(entities = {PrayerModel.class}, version = 1)
public abstract class PrayerDB extends RoomDatabase {
    public abstract PrayerDAO prayerDAO();
}
