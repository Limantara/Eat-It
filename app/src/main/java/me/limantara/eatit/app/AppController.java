package me.limantara.eatit.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.limantara.eatit.R;
import me.limantara.eatit.volley.LruBitmapCache;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class AppController extends Application
        implements ConnectionCallbacks, OnConnectionFailedListener {

    public static final String TAG = AppController.class.getSimpleName();

    public static final int HOME = 0;
    public static final int LATEST_SUGGESTION = 1;
    public static final int RECENT_SUGGESTIONS = 2;
    public static final int SET_BUDGET = 3;
    public static final int SET_DISTANCE = 4;
    public static final int SET_LOCATION = 5;
    public static final int HELP_CENTER = 6;

    public static final String PACKAGE_NAME = "me.limantara.eatit";
    public static final String BUDGET_PREFERENCE = "budget";
    public static final String DISTANCE_PREFERENCE = "distance";
    public static final String LOCATION_PREFERENCE = "location";

    public static final int DEFAULT_BUDGET_PREFERENCE = 11; // 11 USD
    public static final int DEFAULT_DISTANCE_PREFERENCE = 5; // 5 miles
    public static final Float DEFAULT_LATITUDE = new Float(37.3434479);
    public static final Float DEFAULT_LONGITUDE = new Float(-121.8822139);

    private static Integer LOCATION_STATUS_CODE;
    private static final int LOCATION_REQUEST_CODE = 1;
    private static AppController mInstance;
    private static Float latitude;
    private static Float longitude;
    private String address;
    private GoogleApiClient mGoogleApiClient;
    private boolean isLocationAvailable;

    private static final int ADDRESS_MAX_RESULT = 1;
    private static final String UNKNOWN_ADDRESS = "Can't find location";

    private static List<LocationListener> locationListeners = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(lastLocation == null)
            return;

        saveLocationInformation(lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LOCATION_STATUS_CODE = connectionResult.getErrorCode();
    }

    public interface LocationListener {
        public void onLocationEstablished();
    }

    public void addLocationListener(LocationListener listener) {
        locationListeners.add(listener);
    }

    public void notifyListeners() {
        for(LocationListener listener : locationListeners) {
            listener.onLocationEstablished();
        }
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    /**
     * Helper method to show Google location error dialog
     *
     * @param activity
     */
    public void showLocationErrorDialog(Activity activity) {
        GooglePlayServicesUtil.getErrorDialog(LOCATION_STATUS_CODE, activity, LOCATION_REQUEST_CODE).show();
    }

    /**
     * Check if location is available and set status code
     *
     * @return
     */
    public boolean hasLocation() {
        return isLocationAvailable;
    }

    /**
     * Helper method to build Google api client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Helper method to get shared preferences
     *
     * @return
     */
    public SharedPreferences getPreferences() {
        return getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE);
    }

    /**
     * Helper method to get the current address
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Helper method to get the preferred distance
     *
     * @return
     */
    public int getPreferredDistance() {
        return getPreferences().getInt(DISTANCE_PREFERENCE, DEFAULT_DISTANCE_PREFERENCE);
    }

    /**
     * Helper method to get the preferred budget
     *
     * @return
     */
    public int getPreferredBudget() {
        return getPreferences().getInt(BUDGET_PREFERENCE, DEFAULT_BUDGET_PREFERENCE);
    }

    /**
     * Helper method to set the preferred budget
     *
     * @param newBudget
     */
    public void setPreferredBudget(int newBudget) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(BUDGET_PREFERENCE, newBudget);
        editor.apply();
    }

    /**
     * Helper method to save location information and get address asynchronously
     */
    private void saveLocationInformation(Location lastLocation) {
        latitude = new Float(lastLocation.getLatitude());
        longitude = new Float(lastLocation.getLongitude());
        isLocationAvailable = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Save address
                try {
                    Geocoder geocoder = new Geocoder(AppController.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, ADDRESS_MAX_RESULT);
                    address = addresses.get(0).getAddressLine(0);
                }
                catch(IOException e) {
                    address = UNKNOWN_ADDRESS;
                    System.out.println(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyListeners();
            }
        }.execute();
    }

    /**
     * Helper method to get menu at a given position.
     *
     * @param pos
     * @return
     */
    public String getTextMenu(int pos) {
        String[] menu = getResources().getStringArray(R.array.nav_drawer_labels);
        return menu[pos];
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();

        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
