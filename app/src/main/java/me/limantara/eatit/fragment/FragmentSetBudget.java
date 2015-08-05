package me.limantara.eatit.fragment;


import android.app.Fragment;
import android.os.Bundle;
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
public class FragmentSetBudget extends Fragment {

    private AppController app = AppController.getInstance();
    private SeekBar seekBar;
    private TextView budgetValue;

    private static final int BUDGET_OFFSET = 7;

    public FragmentSetBudget() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_set_budget, container, false);
        seekBar = (SeekBar) root.findViewById(R.id.seekBar);
        budgetValue = (TextView) root.findViewById(R.id.budget_value);
        setUp();

        return root;
    }

    /**
     * Helper method to attach listener to seekbar and set the budget value text
     */
    private void setUp() {
        int budget = app.getPreferredBudget();
        budgetValue.setText(String.valueOf(budget));

        seekBar.setProgress(budget - BUDGET_OFFSET);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newBudget = BUDGET_OFFSET + progress;
                budgetValue.setText(String.valueOf(newBudget));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  System.out.println("stop called");
                app.setPreferredBudget(seekBar.getProgress() + BUDGET_OFFSET);
            }
        });
    }

}
