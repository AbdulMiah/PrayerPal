package com.example.mob_dev_portfolio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mob_dev_portfolio.adapters.PrayerListAdapter;
import com.example.mob_dev_portfolio.models.PrayerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrayerTimesFragment extends Fragment {

    private ListView lv;
    private ArrayAdapter<String> arrayAdapter;
    private PrayerListAdapter prayerListAdapter;
    private String[] prayerNamesList;
    private ArrayList<PrayerModel> prayerModels = new ArrayList<PrayerModel>();

    public PrayerTimesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prayer_times, container, false);

        this.lv = (ListView) v.findViewById(R.id.prayer_times_list_view);

        onAPIRequest(v);

        TextView locationBtn = v.findViewById(R.id.prayer_location_btn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAPIRequest(view);
            }
        });

        return v;
//        } else {
//            prayerNamesList = getResources().getStringArray(R.array.prayer_names);
//            this.arrayAdapter = new ArrayAdapter<String>(
//                getActivity(),
//                android.R.layout.simple_list_item_1,
//                prayerNamesList
//            );
//            this.lv.setAdapter(arrayAdapter);
//            return v;
//        }
    }

    public void onAPIRequest(View view) {
        String apiUrl = "https://api.pray.zone/v2/times/today.json?city=cardiff";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest apiRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Empty
                    }
                }
        );
        requestQueue.add(apiRequest);
    }

    public void handleResponse(JSONObject items) {
        if (prayerModels.size() <= 0) {
            prayerNamesList = getResources().getStringArray(R.array.prayer_names);
            int listSize = prayerNamesList.length;
            try {
                JSONArray ja = items.getJSONObject("results").getJSONArray("datetime");

                for (int i=0; i<listSize; i++) {
                    JSONObject jo = ja.getJSONObject(0).getJSONObject("times");
//                Log.i(prayerNamesList[i],jo.getString(prayerNamesList[i]));
                    prayerModels.add(new PrayerModel(prayerNamesList[i], jo.getString(prayerNamesList[i])));
                }
            } catch (JSONException e) {
                Log.e("ERROR!", e.toString());
            }
            Log.i("NEW PRAYER MODEL",prayerModels.toString());
        } else {
            Toast.makeText(getActivity(), "You already have the current prayer times", Toast.LENGTH_SHORT).show();
            Log.i("CURRENT PRAYER MODEL",prayerModels.toString());
        }

        prayerListAdapter = new PrayerListAdapter(lv.getContext(), prayerModels);
        this.lv.setAdapter(prayerListAdapter);

    }

}