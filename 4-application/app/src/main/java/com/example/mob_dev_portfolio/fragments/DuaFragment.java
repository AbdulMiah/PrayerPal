package com.example.mob_dev_portfolio.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.adapters.DuaListAdapter;
import com.example.mob_dev_portfolio.models.Dua;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DuaFragment extends Fragment {

    private ArrayList<Dua> duaArrayList = new ArrayList<>();
    private ArrayList<Dua> testList = new ArrayList<>();
    private DuaListAdapter duaListAdapter;
    private ListView lv;
    private SearchView sv;


    public DuaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dua, container, false);

        // Get bundle data
        this.duaArrayList = (ArrayList<Dua>) getArguments().getSerializable("apiDuaData");

        lv = v.findViewById(R.id.dua_lv);
        sv = v.findViewById(R.id.dua_title_sv);

        getDuas(v);

        return v;
    }

    public void getDuas(View v) {
        // Populate ListView with the dua titles
        duaListAdapter = new DuaListAdapter(lv.getContext(), duaArrayList);
        lv.setAdapter(duaListAdapter);

        // Set click listener to change fragment to DuaDetailFragment on item selected from ListView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                changeInternalFragment(new DuaDetailFragment(), R.id.main_frag_container, duaArrayList.get(i));
            }
        });

        // Set query listener on SearchView and filter ListView according to text change
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                duaListAdapter.getFilter().filter(s);
                return false;
            }
        });
    }

    // Method to change fragment and store dua details into a bundle
    private void changeInternalFragment(Fragment fragment, int fragmentContainer, Dua dua){
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();

        if (dua != null) {
            Bundle bundle = new Bundle();
            bundle.putString("title", dua.getTitle());
            bundle.putString("arabic", dua.getArabic());
            bundle.putString("transliteration", dua.getTransliteration());
            bundle.putString("meaning", dua.getMeaning());
            fragment.setArguments(bundle);
        }

        supportFragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();
    }


}