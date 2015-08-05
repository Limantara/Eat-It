package me.limantara.eatit.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;

public class FragmentHome extends Fragment
    implements AppController.LocationListener {

    private AppController app = AppController.getInstance();
    private SharedPreferences settings;
    private TextView budget;
    private TextView distance;
    private TextView location;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        settings = app.getPreferences();
        app.addLocationListener(this);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        budget = (TextView) root.findViewById(R.id.budget);
        distance = (TextView) root.findViewById(R.id.distance);
        location = (TextView) root.findViewById(R.id.location);

        setBudget(budget);
        setDistance(distance);

        if(app.hasLocation())
            setLocation(location);

        return root;
    }

    @Override
    public void onLocationEstablished() {
        setLocation(location);
    }

    private void setBudget(TextView budget) {
        String budgetPreference = "$ " + settings.getInt(AppController.BUDGET_PREFERENCE, AppController.DEFAULT_BUDGET_PREFERENCE);
        budget.setText(budgetPreference);
    }

    private void setDistance(TextView distance) {
        String distancePreference = settings.getInt(AppController.DISTANCE_PREFERENCE, AppController.DEFAULT_DISTANCE_PREFERENCE) + " miles";
        distance.setText(distancePreference);
    }

    private void setLocation(TextView location) {
        Float latitude = app.getLatitude();
        Float longitude = app.getLongitude();

        if(latitude == null && longitude == null)
            app.showLocationErrorDialog(getActivity());
        else
            location.setText(app.getAddress());
    }
}
