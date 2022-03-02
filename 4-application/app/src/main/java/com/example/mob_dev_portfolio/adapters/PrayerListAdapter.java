package com.example.mob_dev_portfolio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.models.PrayerModel;

import java.util.ArrayList;
import java.util.List;

public class PrayerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PrayerModel> prayerModelList;

    public PrayerListAdapter(Context context, ArrayList<PrayerModel> prayerModelList) {
        this.context = context;
        this.prayerModelList = prayerModelList;
    }

    @Override
    public int getCount() {
        return prayerModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return prayerModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.prayer_listview_adapter, null);
        }
        TextView prayerName = view.findViewById(R.id.prayer_name_text);
        TextView prayerTime = view.findViewById(R.id.prayer_time_text);
        PrayerModel pm = prayerModelList.get(position);
        prayerName.setText(pm.getPrayerName());
        prayerTime.setText(pm.getPrayerTime());

        return view;
    }
}
