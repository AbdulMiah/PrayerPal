package com.example.mob_dev_portfolio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PrayerTimesFragment extends Fragment {

    private ListView lv;
    private ArrayAdapter<String> arrayAdapter;

    public PrayerTimesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prayer_times, container, false);

        String[] prayerNamesList = getResources().getStringArray(R.array.prayer_names);
        this.lv = (ListView) v.findViewById(R.id.prayer_times_list_view);

        this.arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                prayerNamesList
        );
        this.lv.setAdapter(arrayAdapter);
        return v;
    }
}