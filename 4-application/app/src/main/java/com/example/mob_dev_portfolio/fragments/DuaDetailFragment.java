package com.example.mob_dev_portfolio.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mob_dev_portfolio.R;

public class DuaDetailFragment extends Fragment {

    private TextView duaTitle;
    private TextView arabic;
    private TextView transliteration;
    private TextView meaning;
    private AppCompatButton backBtn;

    public DuaDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dua_detail, container, false);

        // Fetch Dua info from bundles
        String titleData = getArguments().getString("title");
        String arabicData = getArguments().getString("arabic");
        String transliterationData = getArguments().getString("transliteration");
        String meaningData = getArguments().getString("meaning");

        this.duaTitle = v.findViewById(R.id.dua_title);
        this.arabic = v.findViewById(R.id.arabic_section);
        this.transliteration = v.findViewById(R.id.transliteration_section);
        this.meaning = v.findViewById(R.id.meaning_section);
        this.backBtn = v.findViewById(R.id.dua_back_btn);

        // Setting the TextViews
        duaTitle.setText(titleData);
        arabic.setText(arabicData);
        transliteration.setText(transliterationData);
        meaning.setText(meaningData);

        // Set onClickListener to back button
        backBtn.setOnClickListener(this::onClick);

        return v;
    }

    // Return back to the DuaFragment
    private void onClick(View view) {
        changeInternalFragment(new DuaFragment(), R.id.main_frag_container);
    }

    private void changeInternalFragment(Fragment fragment, int fragmentContainer){
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();

        supportFragmentManager.beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();
    }
}