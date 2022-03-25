package com.example.mob_dev_portfolio.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tracker {

    @PrimaryKey(autoGenerate = true)
    private int trackerId;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "fajr_prayed")
    private Boolean fajrPrayed;

    @ColumnInfo(name = "dhuhr_prayed")
    private Boolean dhuhrPrayed;

    @ColumnInfo(name = "asr_prayed")
    private Boolean asrPrayed;

    @ColumnInfo(name = "maghrib_prayed")
    private Boolean maghribPrayed;

    @ColumnInfo(name = "isha_prayed")
    private Boolean ishaPrayed;

    public Tracker() {
    }

    public Tracker(int trackerId, String date, Boolean fajrPrayed, Boolean dhuhrPrayed, Boolean asrPrayed, Boolean maghribPrayed, Boolean ishaPrayed) {
        this.trackerId = trackerId;
        this.date = date;
        this.fajrPrayed = fajrPrayed;
        this.dhuhrPrayed = dhuhrPrayed;
        this.asrPrayed = asrPrayed;
        this.maghribPrayed = maghribPrayed;
        this.ishaPrayed = ishaPrayed;
    }

    public int getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getFajrPrayed() {
        return fajrPrayed;
    }

    public void setFajrPrayed(Boolean fajrPrayed) {
        this.fajrPrayed = fajrPrayed;
    }

    public Boolean getDhuhrPrayed() {
        return dhuhrPrayed;
    }

    public void setDhuhrPrayed(Boolean dhuhrPrayed) {
        this.dhuhrPrayed = dhuhrPrayed;
    }

    public Boolean getAsrPrayed() {
        return asrPrayed;
    }

    public void setAsrPrayed(Boolean asrPrayed) {
        this.asrPrayed = asrPrayed;
    }

    public Boolean getMaghribPrayed() {
        return maghribPrayed;
    }

    public void setMaghribPrayed(Boolean maghribPrayed) {
        this.maghribPrayed = maghribPrayed;
    }

    public Boolean getIshaPrayed() {
        return ishaPrayed;
    }

    public void setIshaPrayed(Boolean ishaPrayed) {
        this.ishaPrayed = ishaPrayed;
    }

    @Override
    public String toString() {
        return "Tracker{" +
                "trackerId=" + trackerId +
                ", date='" + date + '\'' +
                ", fajrPrayed=" + fajrPrayed +
                ", dhuhrPrayed=" + dhuhrPrayed +
                ", asrPrayed=" + asrPrayed +
                ", maghribPrayed=" + maghribPrayed +
                ", ishaPrayed=" + ishaPrayed +
                '}';
    }
}
