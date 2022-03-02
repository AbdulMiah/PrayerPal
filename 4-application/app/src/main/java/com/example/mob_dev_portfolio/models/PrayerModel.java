package com.example.mob_dev_portfolio.models;

public class PrayerModel {

    private String prayerName;
    private String prayerTime;

    public PrayerModel(String prayerName, String prayerTime) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
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
