package me.limantara.eatit.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.limantara.eatit.API.BingAPI;
import me.limantara.eatit.API.LocuAPI;
import me.limantara.eatit.Helper.SQLiteHelper;
import me.limantara.eatit.Helper.TimeHelper;
import me.limantara.eatit.Helper.Util;
import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.model.Venue;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FragmentDrawer.FragmentDrawerListener,
        LocationListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private TextView prompt;
    private TextView location;

    public final static String VENUE = "me.limantara.eatitorleaveit.VENUE";
    public final static String FOOD = "me.limantara.eatitorleaveit.FOOD";
    public final static String STORE_FOOD = "me.limantara.eatitorleaveit.STORE_FOOD";
    private static final String DIALOG_ERROR = "dialog_error";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private List<Venue> venues;
    private Venue selectedVenue;
    private SQLiteHelper dbHelper;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private Float latitude = null;
    private Float longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  System.out.println("on create is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = SQLiteHelper.getInstance(this);
        prompt = (TextView) findViewById(R.id.prompt);

        // Set up toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up drawer
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // Set FAB animation
        final FloatingActionButton buttonExplore = (FloatingActionButton) findViewById(R.id.buttonExplore);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        animation.setStartOffset(500);
        buttonExplore.startAnimation(animation);

        boolean maxLimit = checkLimit();
        System.out.println("Limit: " + maxLimit);
        if(maxLimit) {
            prompt.setText("Check back again for " + TimeHelper.getNextEatTime());
            prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
        else {
            prompt.setText("What's for " + TimeHelper.getEatTime() + " ?");
            prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        }

        checkLocationRequirement();

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }

        location = (TextView) findViewById(R.id.location);

        if(AppController.getLatitude() != null && AppController.getLongitude() != null) {
            try {
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(AppController.getLatitude(), AppController.getLongitude(), 1);

                if (addresses.size() > 0)
                    location.setText(addresses.get(0).getLocality());
            }
            catch(IOException e) {
                location.setText("Unknown location");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setPreferences();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            AppController.setLatitude(new Float(mLastLocation.getLatitude()));
            AppController.setLongitude(new Float(mLastLocation.getLongitude()));

            System.out.println(latitude + ", " + longitude);

        } else {

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000); //10 seconds
            mLocationRequest.setFastestInterval(5000); //5 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setSmallestDisplacement(1); //1 meter
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            System.out.println("(Couldn't get the location. Make sure location is enabled on the device)");

            AppController.setLatitude(AppController.DEFAULT_LATITUDE);
            AppController.setLongitude(AppController.DEFAULT_LONGITUDE);
        }

        try {
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(AppController.getLatitude(), AppController.getLongitude(), 1);

            if (addresses.size() > 0)
                location.setText(addresses.get(0).getLocality());
        }
        catch(IOException e) {
            location.setText("Unknown location");
        }

    }

    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            System.out.println("Stop location updates");

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            System.out.println("Periodic location updates started!");

        } else {
            // Changing the button text
            System.out.println("Start location updates");

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            System.out.println("Periodic location updates stopped!");
        }
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        Intent intent;
        switch(position) {
            case 0:
                return;
            case 1:
                intent = new Intent(this, DisplayResult.class);
                break;
            case 2:
                intent = new Intent(this, RecentSuggestion.class);
                break;
            case 3:
                intent = new Intent(this, SetBudget.class);
                break;
            case 4:
                intent = new Intent(this, SetDistance.class);
                break;
            case 5:
                intent = new Intent(this, SetLocation.class);
                break;
            case 6:
                intent = new Intent(this, HelpCenter.class);
                break;
            default:
                return;
        }

        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("CONNECTED TO PLAY SERVICES!!");

        if(latitude == null && longitude == null)
            displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("Connection failed: " + connectionResult.getErrorCode());
        showErrorDialog(connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
    }

    /**
     * Button click listener to make an API call to Locu.com
     *
     * @param view
     */
    public void callLocu(View view) {
        int radius = getSharedPreferences("me.limantara.eatit", 0).getInt("distance", AppController.DEFAULT_DISTANCE_PREFERENCE);
        boolean maxLimit = checkLimit();

        if( ! maxLimit)
            new LocuAPI(this, radius).makeApiCall();
    }

    /**
     * Select a random food from a list of restaurants and
     * find its pictures on Bing
     *
     * @param venueList
     */
    public void findFood(List<Venue> venueList) {
        this.venues = venueList;
        int budget = getSharedPreferences("me.limantara.eatit", 0).getInt("budget", AppController.DEFAULT_BUDGET_PREFERENCE);

        do {

            selectedVenue = pickAVenue();

        } while( ! venues.isEmpty() && selectedVenue == null);


        if(selectedVenue != null)
            new BingAPI(this, selectedVenue.getFoods(budget)).makeApiCall();
        else
            System.out.println("Can't find anything");
    }

    /**
     * Find another venue if none of the food in this place has
     * a picture on Bing.
     */
    public void findAnotherVenue() {
        System.out.println("Find another venue is called");
        findFood(venues);
    }

    /**
     * Move on to the next activity - displaying the food result
     */
    public void displayResult(Venue.Item selectedFood, boolean storeFood) {
        Intent intent = new Intent(this, DisplayResult.class);

        if(selectedVenue == null)
            selectedVenue = SQLiteHelper.getInstance(this).findVenueFromFood(selectedFood);

        intent.putExtra(VENUE, selectedVenue);
        intent.putExtra(FOOD, selectedFood);
        intent.putExtra(STORE_FOOD, storeFood);

        startActivity(intent);
    }

    /**
     * Get a random venue and remove it from the list.
     *
     * @return
     */
    private Venue pickAVenue() {
        Venue venue = null;
        int budget = getSharedPreferences("me.limantara.eatit", 0).getInt("budget", AppController.DEFAULT_BUDGET_PREFERENCE);

        while( ! venues.isEmpty()) {
            int i = Util.getRandomNumber(0, venues.size());
            venue = venues.get(i);
            venues.remove(i);

            if( ! venue.getFoods(budget).isEmpty()) {
                System.out.println("foods: " + venue.getFoods(budget));
                break;
            }
        }

        return venue;
    }

    /**
     * Check if the limit (one suggestion per eat time) has been reached
     *
     * @return
     */
    private boolean checkLimit() {
        Venue.Item latestFood = dbHelper.getLatestFood();
        Long current_eat_time = TimeHelper.getEatTimeObject().getTimeInMillis();
        System.out.println("Current: " + current_eat_time + ", last: " + latestFood.created_at);
        System.out.println(latestFood);
        if(latestFood != null)
            return latestFood.created_at >= current_eat_time;
        else
            return false;
    }

    /**
     * Get and display user preferences
     */
    private void setPreferences() {  System.out.println("Set preferences is called");
        SharedPreferences settings = getSharedPreferences("me.limantara.eatit", 0);

        // Get preferences
        int budgetPreference = settings.getInt("budget", AppController.DEFAULT_BUDGET_PREFERENCE);
        int distancePreference = settings.getInt("distance", AppController.DEFAULT_DISTANCE_PREFERENCE);

        // Get text views
        TextView budget = (TextView) findViewById(R.id.budget);
        TextView distance = (TextView) findViewById(R.id.distance);

        // Display preferences
        budget.setText("$ " + budgetPreference + " USD");
        distance.setText(distancePreference + " miles");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void checkLocationRequirement() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("GPS is not enabled");

            dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.show();
        }
        else {
            System.out.println("GPS AND NETWORK ARE ENABLED");
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        Dialog googleDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, 1);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Play Services Error");

        dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        googleDialog.show();
    }
}
