package com.example.mob_dev_portfolio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.models.Dua;

import java.util.ArrayList;

public class DuaListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Dua> duas;

    public DuaListAdapter(Context context, ArrayList<Dua> duas) {
        this.context = context;
        this.duas = duas;
    }

    @Override
    public int getCount() {
        return duas.size();
    }

    @Override
    public Object getItem(int i) {
        return duas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dua_lv_adapter, null);
        }
        TextView duaTitle = view.findViewById(R.id.dua_item_title);
        Dua dua = duas.get(position);
        duaTitle.setText(dua.getTitle());

        return view;
    }
}
