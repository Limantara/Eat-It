package me.limantara.eatit.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.limantara.eatit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRecentSuggestions extends Fragment {


    public FragmentRecentSuggestions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_suggestions, container, false);
    }


}
