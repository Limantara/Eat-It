package me.limantara.eatit.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetLocation extends Fragment {

    private AppController app = AppController.getInstance();
    private TextView cityName;

    public FragmentSetLocation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_set_location, container, false);
        cityName = (TextView) root.findViewById(R.id.city_name);

        if(app.hasLocation())
            cityName.setText(app.getAddress());

        return root;
    }



}
