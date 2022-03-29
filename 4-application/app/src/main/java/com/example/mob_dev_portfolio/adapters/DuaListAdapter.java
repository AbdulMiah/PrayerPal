package com.example.mob_dev_portfolio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.models.Dua;

import java.util.ArrayList;

public class DuaListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Dua> duas;
    private ArrayList<Dua> duasFiltered;

    public DuaListAdapter(Context context, ArrayList<Dua> duas) {
        this.context = context;
        this.duas = duas;
        this.duasFiltered = duas;
    }

    @Override
    public int getCount() {
        return this.duasFiltered.size();
    }

    @Override
    public Object getItem(int i) {
        return duasFiltered.get(i);
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
        Dua dua = duasFiltered.get(position);
        duaTitle.setText(dua.getTitle());

        return view;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                ArrayList<Dua> duasFiltered = new ArrayList<>();

                for (Dua d : duas) {
                    if(d.getTitle().toLowerCase().contains(charString)) {
                        duasFiltered.add(d);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = duasFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                System.out.println(filterResults.values);
                duasFiltered = (ArrayList<Dua>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
