package me.limantara.eatit.API;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.limantara.eatit.Helper.VolleyHelper;
import me.limantara.eatit.R;
import me.limantara.eatit.activity.MainActivity;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.model.Venue;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class LocuAPI implements Response.Listener<JSONObject>, Response.ErrorListener {

    private JsonObjectRequest postRequest;
    private MainActivity mainActivity;
    private int radius;
    private Float latitude;
    private Float longitude;

    public LocuAPI(MainActivity activity, int rMiles) {
        mainActivity = activity;
        radius = rMiles * 1609;
        latitude = AppController.getLatitude();
        longitude = AppController.getLongitude();
    }

    /**
     * Make an asynchronous API call to Locu and get a list of restaurants.
     */
    public void makeApiCall() {
        final String LocuURL = "https://api.locu.com/v2/venue/search";

        try {
            JSONObject requestParams = fillParams();

            postRequest = new JsonObjectRequest(Request.Method.POST, LocuURL,
                    requestParams, LocuAPI.this, LocuAPI.this);

            VolleyHelper.getInstance(mainActivity).addToRequestObject(postRequest);
        }
        catch(JSONException e) {
            System.out.println(e);
        }
    }

    /**
     * Convert the list of json response objects to Venue objects and
     * call main activity to select a random food from the newly retrieved list of venues.
     *
     * @param jsonObject
     */
    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            List<Venue> venueList = new ArrayList<>();
            JSONArray venues = jsonObject.getJSONArray("venues");

            System.out.println("Locu response: " + jsonObject.toString());

            for(int i = 0; i < venues.length(); i++) {
                JSONObject venueJSON = venues.getJSONObject(i);
                JsonObject googleJSON =
                        (JsonObject) new JsonParser().parse(venueJSON.toString());
                Venue venue = new Gson().fromJson(googleJSON, Venue.class);

                JSONObject locationJSON = venueJSON.getJSONObject("location");
                JSONObject geoJSON = locationJSON.getJSONObject("geo");
                JSONArray coordinatesJSON = geoJSON.getJSONArray("coordinates");
                Float latitude = Float.parseFloat(coordinatesJSON.getString(1));
                Float longitude = Float.parseFloat(coordinatesJSON.getString(0));

                venue.setLatitude(latitude);
                venue.setLongitude(longitude);
                venueList.add(venue);
            }

            mainActivity.findFood(venueList);
        }
        catch(JSONException e) {
            System.out.println(e);
        }
    }

    /**
     * Print out Volley error message.
     *
     * @param volleyError
     */
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        System.out.println(volleyError);
    }

    /**
     * Helper method to fill Locu API parameters
     *
     * @return
     * @throws JSONException
     */
    private JSONObject fillParams() throws JSONException {
        JSONObject params = new JSONObject();
        String api_key = mainActivity.getString(R.string.locu_api_key);

        String[] fields = {"locu_id", "name", "location", "menus", "categories"};
        JSONObject[] venue_queries = { getVenueQueries() };

        params.put("api_key", api_key);
        params.put("fields", new JSONArray(Arrays.asList(fields)));
        params.put("venue_queries", new JSONArray(Arrays.asList(venue_queries)));

        return params;
    }

    /**
     * Helper method to contruct venue queries object.
     *
     * @return
     * @throws JSONException
     */
    private JSONObject getVenueQueries() throws JSONException {
        JSONObject venueQueries = new JSONObject();

        venueQueries.put("categories",
                new JSONObject("{ \"$contains_any\": [\"restaurants\"]}"));
        venueQueries.put("location",
                new JSONObject("{\"geo\": {\"$in_lat_lng_radius\": ["+latitude+", "+longitude+", " + radius + "]}}"));
        venueQueries.put("menus", new JSONObject("{\"$present\" : true}"));

        return venueQueries;
    }

}
