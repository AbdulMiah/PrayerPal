package com.example.mob_dev_portfolio.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PrayerModel {

    @PrimaryKey(autoGenerate = true)
    private int prayerId;

    @ColumnInfo(name = "prayer_name")
    private String prayerName;

    @ColumnInfo(name = "prayer_time")
    private String prayerTime;

    public PrayerModel(String prayerName, String prayerTime) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
    }

    public int getPrayerId() {
        return prayerId;
    }

    public void setPrayerId(int prayerId) {
        this.prayerId = prayerId;
    }

    public String getPrayerName() {
        return prayerName;
    }

    public void setPrayerName(String prayerName) {
        this.prayerName = prayerName;
    }

    public String getPrayerTime() {
        return prayerTime;
    }

    public void setPrayerTime(String prayerTime) {
        this.prayerTime = prayerTime;
    }

    @Override
    public String toString() {
        return "PrayerModel{" +
                "prayerName='" + prayerName + '\'' +
                ", prayerTime='" + prayerTime + '\'' +
                '}';
    }
}
