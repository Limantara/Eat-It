package me.limantara.eatit.activity;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.fragment.FragmentHelpCenter;
import me.limantara.eatit.fragment.FragmentHome;
import me.limantara.eatit.fragment.FragmentLatestSuggestion;
import me.limantara.eatit.fragment.FragmentRecentSuggestions;
import me.limantara.eatit.fragment.FragmentSetBudget;
import me.limantara.eatit.fragment.FragmentSetDistance;
import me.limantara.eatit.fragment.FragmentSetLocation;
import me.limantara.eatit.model.Venue;

//implements
//        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener


public class MainActivity extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener {

    private AppController app = AppController.getInstance();
    private static int CURRENT_STATE = AppController.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpDrawer();
        fillContent();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        if(CURRENT_STATE == position)
            return;

        switchFragment(position);
    }

    /**
     * Helper method to set up the navigation drawer
     */
    private void setUpDrawer() {
        FragmentDrawer drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.drawer);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerFragment.setUp(R.id.drawer, drawerLayout, toolbar);
        drawerFragment.setDrawerListener(this);
    }

    private void switchFragment(int state) {
        CURRENT_STATE = state;
        setToolbarTitle();
        fillContent();
    }

    /**
     * Helper method to set toolbar title based on the current state of the application
     */
    private void setToolbarTitle() {
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);

        if(CURRENT_STATE == AppController.HOME)
            toolbarTitle.setText("");
        else
            toolbarTitle.setText(app.getTextMenu(CURRENT_STATE));
    }

    /**
     * Helper method to switch fragment
     */
    private void fillContent() {
        Fragment fragment = createFragment(CURRENT_STATE);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.content_frame, fragment)
                       .commit();
    }

    /**
     * Helper method to create a content fragment based on the current application state
     *
     * @param state
     * @return
     */
    private Fragment createFragment(int state) {
        switch(state) {
            case AppController.HOME:
                return new FragmentHome();
            case AppController.LATEST_SUGGESTION:
                return new FragmentLatestSuggestion();
            case AppController.RECENT_SUGGESTIONS:
                return new FragmentRecentSuggestions();
            case AppController.SET_BUDGET:
                return new FragmentSetBudget();
            case AppController.SET_DISTANCE:
                return new FragmentSetDistance();
            case AppController.SET_LOCATION:
                return new FragmentSetLocation();
            case AppController.HELP_CENTER:
                return new FragmentHelpCenter();
            default:
                return null;
        }
    }



//    private void unbindDrawables(View view) {
//        if (view.getBackground() != null)
//            view.getBackground().setCallback(null);
//
//        if (view instanceof ViewGroup) {
//            ViewGroup viewGroup = (ViewGroup) view;
//
//            for (int i = 0; i < viewGroup.getChildCount(); i++)
//                unbindDrawables(viewGroup.getChildAt(i));
//
//            if (!(view instanceof AdapterView) && !(view instanceof RecyclerView))
//                viewGroup.removeAllViews();
//        }
//    }

    /**
     * Method to display the location on UI
     * */
//    private void displayLocation() {
//
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        if (mLastLocation != null) {
//            AppController.setLatitude(new Float(mLastLocation.getLatitude()));
//            AppController.setLongitude(new Float(mLastLocation.getLongitude()));
//
//            System.out.println(latitude + ", " + longitude);
//
//        } else {
//
//            mLocationRequest = new LocationRequest();
//            mLocationRequest.setInterval(10000); //10 seconds
//            mLocationRequest.setFastestInterval(5000); //5 seconds
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//            mLocationRequest.setSmallestDisplacement(1); //1 meter
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            System.out.println("(Couldn't get the location. Make sure location is enabled on the device)");
//
//            AppController.setLatitude(AppController.DEFAULT_LATITUDE);
//            AppController.setLongitude(AppController.DEFAULT_LONGITUDE);
//        }
//
//        try {
//            Geocoder gcd = new Geocoder(this, Locale.getDefault());
//            List<Address> addresses = gcd.getFromLocation(AppController.getLatitude(), AppController.getLongitude(), 1);
//
//            if (addresses.size() > 0)
//                location.setText(addresses.get(0).getLocality());
//        }
//        catch(IOException e) {
//            location.setText("Unknown location");
//        }
//
//    }

    /**
     * Method to toggle periodic location updates
     * */
//    private void togglePeriodicLocationUpdates() {
//        if (!mRequestingLocationUpdates) {
//            System.out.println("Stop location updates");
//
//            mRequestingLocationUpdates = true;
//
//            // Starting the location updates
//            startLocationUpdates();
//
//            System.out.println("Periodic location updates started!");
//
//        } else {
//            // Changing the button text
//            System.out.println("Start location updates");
//
//            mRequestingLocationUpdates = false;
//
//            // Stopping the location updates
//            stopLocationUpdates();
//
//            System.out.println("Periodic location updates stopped!");
//        }
//    }

    /**
     * Creating location request object
     * */
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
//    }

    /**
     * Method to verify google play services on the device
     * */
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "This device is not supported.", Toast.LENGTH_LONG)
//                        .show();
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }

    /**
     * Starting the location updates
     * */
//    protected void startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }

    /**
     * Stopping location updates
     */
//    protected void stopLocationUpdates() {
//        if(mGoogleApiClient.isConnected())
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onDrawerItemSelected(View view, int position) {
//        Intent intent;
//        switch(position) {
//            case 0:
//                return;
//            case 1:
//                intent = new Intent(this, DisplayResult.class);
//                break;
//            case 2:
//                intent = new Intent(this, RecentSuggestion.class);
//                break;
//            case 3:
//                intent = new Intent(this, SetBudget.class);
//                break;
//            case 4:
//                intent = new Intent(this, SetDistance.class);
//                break;
//            case 5:
//                intent = new Intent(this, SetLocation.class);
//                break;
//            case 6:
//                intent = new Intent(this, HelpCenter.class);
//                break;
//            default:
//                return;
//        }
//
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        System.out.println("CONNECTED TO PLAY SERVICES!!");
//
//        if(latitude == null && longitude == null)
//            displayLocation();
//
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        System.out.println("Connection suspended");
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        System.out.println("Connection failed: " + connectionResult.getErrorCode());
//        showErrorDialog(connectionResult.getErrorCode());
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        // Assign the new location
//        mLastLocation = location;
//
//        Toast.makeText(getApplicationContext(), "Location changed!",
//                Toast.LENGTH_SHORT).show();
//
//        // Displaying the new location on UI
//        displayLocation();
//    }
//
//    /**
//     * Button click listener to make an API call to Locu.com
//     *
//     * @param view
//     */
//    public void callLocu(View view) {
//        int radius = getSharedPreferences("me.limantara.eatit", 0).getInt("distance", AppController.DEFAULT_DISTANCE_PREFERENCE);
//        boolean maxLimit = checkLimit();
//
//        if(maxLimit) {  System.out.println("Called");
//            String text = TimeHelper.getNextEatTime() + " opens at " + TimeHelper.getNextEatTimeHour();
//            Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
//        }
//        else {
//            String text = "Finding nearby restaurants...";
//            findRestaurant = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_INDEFINITE);
//            findRestaurant.show();
//            new LocuAPI(this, radius).makeApiCall();
//        }
//    }
//
//    /**
//     * Select a random food from a list of restaurants and
//     * find its pictures on Bing
//     *
//     * @param venueList
//     */
    public void findFood(List<Venue> venueList) {
//        this.venues = venueList;
//        int budget = getSharedPreferences("me.limantara.eatit", 0).getInt("budget", AppController.DEFAULT_BUDGET_PREFERENCE);
//
//        do {
//
//            selectedVenue = pickAVenue();
//
//        } while( ! venues.isEmpty() && selectedVenue == null);
//
//
//        if(selectedVenue != null)
//            new BingAPI(this, selectedVenue.getFoods(budget)).makeApiCall();
//        else
//            System.out.println("Can't find anything");
    }
//
//    /**
//     * Find another venue if none of the food in this place has
//     * a picture on Bing.
//     */
    public void findAnotherVenue() {
//        System.out.println("Find another venue is called");
//        findFood(venues);
    }
//
//    /**
//     * Move on to the next activity - displaying the food result
//     */
    public void displayResult(Venue.Item selectedFood, boolean storeFood) {
//        Intent intent = new Intent(this, DisplayResult.class);
//
//        if(selectedVenue == null)
//            selectedVenue = SQLiteHelper.getInstance(this).findVenueFromFood(selectedFood);
//
//        intent.putExtra(VENUE, selectedVenue);
//        intent.putExtra(FOOD, selectedFood);
//        intent.putExtra(STORE_FOOD, storeFood);
//
//        findRestaurant.dismiss();
//        startActivity(intent);
    }
//
//    /**
//     * Get a random venue and remove it from the list.
//     *
//     * @return
//     */
//    private Venue pickAVenue() {
//        Venue venue = null;
//        int budget = getSharedPreferences("me.limantara.eatit", 0).getInt("budget", AppController.DEFAULT_BUDGET_PREFERENCE);
//
//        while( ! venues.isEmpty()) {
//            int i = Util.getRandomNumber(0, venues.size());
//            venue = venues.get(i);
//            venues.remove(i);
//
//            if( ! venue.getFoods(budget).isEmpty()) {
//                System.out.println("foods: " + venue.getFoods(budget));
//                break;
//            }
//        }
//
//        return venue;
//    }
//
//    /**
//     * Check if the limit (one suggestion per eat time) has been reached
//     *
//     * @return
//     */
//    private boolean checkLimit() {
//        Venue.Item latestFood = dbHelper.getLatestFood();
//        Long current_eat_time = TimeHelper.getEatTimeObject().getTimeInMillis();
//
//        if(latestFood != null)
//            return latestFood.created_at >= current_eat_time;
//        else
//            return false;
//    }
//
//    /**
//     * Get and display user preferences
//     */
//    private void setPreferences() {  System.out.println("Set preferences is called");
//        SharedPreferences settings = getSharedPreferences("me.limantara.eatit", 0);
//
//        // Get preferences
//        int budgetPreference = settings.getInt("budget", AppController.DEFAULT_BUDGET_PREFERENCE);
//        int distancePreference = settings.getInt("distance", AppController.DEFAULT_DISTANCE_PREFERENCE);
//
//        // Get text views
//        TextView budget = (TextView) findViewById(R.id.budget);
//        TextView distance = (TextView) findViewById(R.id.distance);
//
//        // Display preferences
//        budget.setText("$ " + budgetPreference + " USD");
//        distance.setText(distancePreference + " miles");
//    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }
//
//    private void checkLocationRequirement() {
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch(Exception ex) {}
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
//
//        if(!gps_enabled && !network_enabled) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//            dialog.setMessage("GPS is not enabled");
//
//            dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(intent);
//                }
//            });
//
//            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//
//            dialog.show();
//        }
//        else {
//            System.out.println("GPS AND NETWORK ARE ENABLED");
//        }
//    }
//
//    /* Creates a dialog for an error message */
//    private void showErrorDialog(int errorCode) {
//        Dialog googleDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, 1);
//
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage("Play Services Error");
//
//        dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        });
//
//        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        googleDialog.show();
//    }
}


//        dbHelper = SQLiteHelper.getInstance(this);
//        prompt = (TextView) findViewById(R.id.prompt);

// Set up toolbar
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

// Set up drawer
//        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
//        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
//        drawerFragment.setDrawerListener(this);

// Set FAB animation
//        final FloatingActionButton buttonExplore = (FloatingActionButton) findViewById(R.id.buttonExplore);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
//        animation.setStartOffset(500);
//        buttonExplore.startAnimation(animation);

//        boolean maxLimit = checkLimit(); System.out.println("Limit: " + maxLimit);
//
//        if(maxLimit) {
//            prompt.setText("Check back again for " + TimeHelper.getNextEatTime());
//            prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//        }
//        else {
//            prompt.setText("What's for " + TimeHelper.getEatTime() + " ?");
//            prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
//        }

//        checkLocationRequirement();

//        if (checkPlayServices()) {
//            buildGoogleApiClient();
//            createLocationRequest();
//        }

//        location = (TextView) findViewById(R.id.location);
//
//        if(AppController.getLatitude() != null && AppController.getLongitude() != null) {
//            try {
//                Geocoder gcd = new Geocoder(this, Locale.getDefault());
//                List<Address> addresses = gcd.getFromLocation(AppController.getLatitude(), AppController.getLongitude(), 1);
//
//                if (addresses.size() > 0)
//                    location.setText(addresses.get(0).getLocality());
//            }
//            catch(IOException e) {
//                location.setText("Unknown location");
//            }
//        }


/******/


//        setPreferences();

//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }


/*******/
//        checkPlayServices();

// Resuming the periodic location updates
//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//
//            startLocationUpdates();
//        }


/*******/

//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }


/****/
//private Toolbar mToolbar;
//private FragmentDrawer drawerFragment;
//private TextView prompt;
//private TextView location;
//
//public final static String VENUE = "me.limantara.eatitorleaveit.VENUE";
//public final static String FOOD = "me.limantara.eatitorleaveit.FOOD";
//public final static String STORE_FOOD = "me.limantara.eatitorleaveit.STORE_FOOD";
//private static final String DIALOG_ERROR = "dialog_error";
//private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
//
//private List<Venue> venues;
//private Venue selectedVenue;
//private SQLiteHelper dbHelper;
//
//private GoogleApiClient mGoogleApiClient;
//private Location mLastLocation;
//private boolean mRequestingLocationUpdates = false;
//private LocationRequest mLocationRequest;
//
//private static int UPDATE_INTERVAL = 10000; // 10 sec
//private static int FATEST_INTERVAL = 5000; // 5 sec
//private static int DISPLACEMENT = 10; // 10 meters
//
//private Float latitude = null;
//private Float longitude = null;
//private Snackbar findRestaurant;