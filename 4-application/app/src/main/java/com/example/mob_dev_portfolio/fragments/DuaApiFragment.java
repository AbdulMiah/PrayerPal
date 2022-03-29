package com.example.mob_dev_portfolio.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.models.Dua;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DuaApiFragment extends Fragment {

    private ArrayList<Dua> apiDuaList = new ArrayList<>();
    private ListView lv;

    public DuaApiFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dua_api, container, false);

        // Set the LoadingFrag onCreate
        changeInternalFragment(new LoadingFragment(), R.id.dua_api_frag_container, null);

        // Get ListView from DuaFragment layout
        View duaFrag = inflater.inflate(R.layout.fragment_dua, container, false);
        lv = duaFrag.findViewById(R.id.dua_lv);

        // Request API
        onAPIRequest(v);

        return v;
    }

    public void onAPIRequest(View view) {
        // API URL
        String apiUrl = "https://abdulmiah.pythonanywhere.com/api/getAllDuas";
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());

        // On API request, send the JSON Object response to the handleResponse() method
        JsonObjectRequest apiRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleResponse(response);
                        changeInternalFragment(new DuaFragment(), R.id.dua_api_frag_container, apiDuaList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REQUEST FROM API ERROR", String.valueOf(error));
                    }
                }
        );
        requestQueue.add(apiRequest);
    }

    public void handleResponse(JSONObject items) {
        try {
            // Iterate through JSON response and get data
            for (int i=0; i<items.length(); i++) {
                JSONObject resultsJson = items.getJSONObject(String.valueOf(i));
                int id = resultsJson.getInt("id");
                String title = resultsJson.getString("title");
                String arabic = resultsJson.getString("arabic");
                String transliteration = resultsJson.getString("transliteration");
                String meaning = resultsJson.getString("meaning");
                // Add the data to an ArrayList of Dua
                apiDuaList.add(new Dua(id, title, arabic, transliteration, meaning));
            }
            // Catch any JSONExceptions and Log to console
        } catch (JSONException e) {
            Log.e("ERROR!", e.toString());
        }
    }

    private void changeInternalFragment(Fragment fragment, int fragmentContainer, ArrayList<Dua> data){
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();

        if (data != null) {
            Bundle b = new Bundle();
            b.putSerializable("apiDuaData", apiDuaList);
            fragment.setArguments(b);
        }

        supportFragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();
    }
}