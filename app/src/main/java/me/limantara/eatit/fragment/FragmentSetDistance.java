package me.limantara.eatit.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetDistance extends Fragment {

    private AppController app = AppController.getInstance();
    private SeekBar seekBar;
    private TextView distanceValue;

    private static final int DISTANCE_OFFSET = 1;

    public FragmentSetDistance() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_set_distance, container, false);
        seekBar = (SeekBar) root.findViewById(R.id.seekBar);
        distanceValue = (TextView) root.findViewById(R.id.distance_value);
        setUp();

        return root;
    }

    /**
     * Helper method to attach listener to seekbar and set the distance value text
     */
    private void setUp() {
        int distance = app.getPreferredDistance();
        distanceValue.setText(String.valueOf(distance));

        seekBar.setProgress(distance - DISTANCE_OFFSET);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newDistance = DISTANCE_OFFSET + progress;
                distanceValue.setText(String.valueOf(newDistance));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                app.setPreferredDistance(seekBar.getProgress() + DISTANCE_OFFSET);
            }
        });
    }

}
