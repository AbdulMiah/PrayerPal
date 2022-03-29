package com.example.mob_dev_portfolio.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mob_dev_portfolio.DAOs.TrackerDAO;
import com.example.mob_dev_portfolio.models.Tracker;

@Database(entities = {Tracker.class}, version = 1)
public abstract class TrackerDB extends RoomDatabase {
    public abstract TrackerDAO trackerDAO();
}
