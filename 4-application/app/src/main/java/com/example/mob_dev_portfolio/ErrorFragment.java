package com.example.mob_dev_portfolio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ErrorFragment extends Fragment {

    private TextView errorTitle;
    private TextView errorMsg;
    private Button returnBtn;

    public ErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_error, container, false);

        // Retrieve error title and message
        String errTitle = getArguments().getString("error title");
        String errMessage = getArguments().getString("error message");

        errorTitle = v.findViewById(R.id.error_title);
        errorMsg = v.findViewById(R.id.error_message);
        returnBtn = v.findViewById(R.id.return_btn);

        // Set the texts
        errorTitle.setText(errTitle);
        errorMsg.setText(errMessage);

        // Add onClickListener to return back to the prayer fragment
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeInternalFragment(new PrayerTimesFragment(), R.id.main_frag_container);
            }
        });

        return v;
    }

    // Method to replace internal fragment
    private void changeInternalFragment(Fragment fragment, int fragmentContainer){
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();

        supportFragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();
    }

}