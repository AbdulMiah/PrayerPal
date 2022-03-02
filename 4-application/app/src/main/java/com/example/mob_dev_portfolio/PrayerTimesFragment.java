package com.example.mob_dev_portfolio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PrayerTimesFragment extends Fragment {

    private ListView lv;
    private PrayerListAdapter prayerListAdapter;
    private String[] prayerNamesList;
    private ArrayList<PrayerModel> prayerModels = new ArrayList<PrayerModel>();
    private Button locationBtn;
    private TextView currentDateText;
    private TextView currentPrayerText;

    public PrayerTimesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prayer_times, container, false);

        this.locationBtn = (Button) v.findViewById(R.id.prayer_location_btn);
        this.currentDateText = (TextView) v.findViewById(R.id.current_date_text);
        this.currentPrayerText = (TextView) v.findViewById(R.id.current_prayer_text);
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
    }

    public void onAPIRequest(View view) {
        // API URL
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
        // Check if ArrayList of PrayerModel is empty, if it is then do this...
        if (prayerModels.size() <= 0) {
            // Initialise some variables
            String city = "";
            String country = "";
            String currentDate = "";
            prayerNamesList = getResources().getStringArray(R.array.prayer_names);
            int listSize = prayerNamesList.length;

            try {
                // Setting JSON Objects and Arrays
                JSONObject resultsJson = items.getJSONObject("results");
                JSONArray datetimeArray = resultsJson.getJSONArray("datetime");
                JSONObject locationObject = resultsJson.getJSONObject("location");

                // Setting current location button to prayer location from API
                city = locationObject.getString("city");
                country = locationObject.getString("country");
                String prayerLocation = city.concat(", "+country);
                this.locationBtn.setText(prayerLocation);
//                Log.i("PRAYER LOCATION",prayerLocation);

                // Looping through datetime array to get prayer times and storing them in an ArrayList of PrayerModel objects
                for (int i=0; i<listSize; i++) {
                    JSONObject prayerTimes = datetimeArray.getJSONObject(0).getJSONObject("times");
//                Log.i(prayerNamesList[i],prayerTimes.getString(prayerNamesList[i]));
                    prayerModels.add(new PrayerModel(prayerNamesList[i], prayerTimes.getString(prayerNamesList[i])));
                }

                // Get current date from API and format it, then set the current date text to formatted date
                currentDate = datetimeArray.getJSONObject(0).getJSONObject("date").getString("gregorian");
                DateTimeFormatter apiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
                LocalDate formatDate = LocalDate.parse(currentDate, apiFormat);
                String output = myFormat.format(formatDate);
                currentDateText.setText(output);
//                Log.i("CURRENT DATE IS...", output);

            // Catch any JSONExceptions and Log to console
            } catch (JSONException e) {
                Log.e("ERROR!", e.toString());
            }
        // If ArrayList of PrayerModel is not empty, then display a Toast
        } else {
            Toast.makeText(getActivity(), "You already have the current prayer times", Toast.LENGTH_SHORT).show();
            Log.i("CURRENT PRAYER MODEL",prayerModels.toString());
        }

        // Populate ListView with the prayer times and names
        prayerListAdapter = new PrayerListAdapter(lv.getContext(), prayerModels);
        this.lv.setAdapter(prayerListAdapter);

        // Get the current time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime timeNow = LocalTime.now();

        // Iterate through the prayer models
        for (PrayerModel pm : prayerModels) {
            LocalTime prayerTime = LocalTime.parse(pm.getPrayerTime());             // Parse the prayer time into LocalTime

            // If the current prayer time is after the prayer times from the model, then set the current prayer text to that prayer name
            if (timeNow.isAfter(prayerTime)) {
                currentPrayerText.setText(pm.getPrayerName());
//                Log.i("PRAYER TIME",pm.getPrayerName());
            }
        }
    }

}