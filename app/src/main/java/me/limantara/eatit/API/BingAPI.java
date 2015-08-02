package me.limantara.eatit.API;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.limantara.eatit.Helper.Util;
import me.limantara.eatit.Helper.VolleyHelper;
import me.limantara.eatit.R;
import me.limantara.eatit.activity.MainActivity;
import me.limantara.eatit.model.Venue;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class BingAPI implements Response.Listener<String>, Response.ErrorListener {
    private MainActivity mainActivity;
    private List<Venue.Item> foods;
    Venue.Item selectedFood;
    private static String BingURL = "https://api.datamarket.azure.com/Bing/Search/Image?";

    public BingAPI(MainActivity activity, List<Venue.Item> foods) {
        mainActivity = activity;
        this.foods = foods;
    }

    /**
     * Get Bing images of a particular food
     */
    public void makeApiCall() {
        selectedFood = pickAFood();

        System.out.println("On CALL: ++++++");
        System.out.println("Food: " + selectedFood);

        if(selectedFood != null) {
            String[] params = fillParams(selectedFood);
            String encodedParams = StringUtils.join(params, "&");
            System.out.println("Bing URL: " + BingURL + encodedParams);
            StringRequest request = new StringRequest(Request.Method.GET,
                    BingURL + encodedParams, BingAPI.this, BingAPI.this
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return BingAPI.this.fillHeaders();
                }
            };

            VolleyHelper.getInstance(mainActivity).addToRequestObject(request);
        }
        else { System.out.println("Calling findAnotherVenue from BingAPI");
            mainActivity.findAnotherVenue();
        }
    }

    /**
     *
     * @param s
     */
    @Override
    public void onResponse(String s) {
        System.out.println(s);

        List<String> images = getImages(s);
        System.out.println("On response: ++++++");
        System.out.println("Food: " + selectedFood);
        System.out.println("Name: " + selectedFood.name);
        System.out.println("Price: " + selectedFood.price);
        System.out.println("Description: " + selectedFood.description);

        // If Bing has the food pictures
        if(images.size() > 0) {
            selectedFood.images = images;
            mainActivity.moveOn(selectedFood, true);
        }
        else {
            mainActivity.findAnotherVenue();
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
     * Helper method to fill Bing request parameters
     *
     * @return
     */
    private String[] fillParams(Venue.Item food) {
        try {
            String term = food.name;
            String[] params = {
                    "Query=" + URLEncoder.encode("'" + term + "'", "UTF-8").replace("+", "%20"),
                    "Market=" + URLEncoder.encode("'en-US'", "UTF-8"),
                    "ImageFilters=" + URLEncoder.encode("'Size:Large'", "UTF-8"),
                    "Adult=" + URLEncoder.encode("'Strict'", "UTF-8"),
                    "Latitude=37.3387261",
                    "Longitude=-121.8822042",
                    "$format=json",
                    "$top=5"
            };
            return params;
        }
        catch(UnsupportedEncodingException e) {
            System.out.println(e);
            String[] params = {};
            return params;
        }
    }

    /**
     * Helper method to fill Bing request headers
     *
     * @return
     */
    private Map<String, String> fillHeaders() {
        Map<String, String> headers = new HashMap<>();
        String bing_primary_key = mainActivity.getString(R.string.bing_primary_key);

        String credentials = String.format("%s:%s", "", bing_primary_key);
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
        return headers;
    }

    /**
     * Get a random food and remove it from the list
     *
     * @return
     */
    private Venue.Item pickAFood() { System.out.println("Pick a food was called");
        Venue.Item food = null;
        while( ! foods.isEmpty()) {
            System.out.println("Foods size: " + foods.size());
            int i = Util.getRandomNumber(0, foods.size());
            food = foods.get(i);
            if(food != null && ! food.equals("null") &&
                    food.name != null && ! food.name.equals("null") &&
                    food.price != null && ! food.price.equals("null") &&
                    food.description != null && ! food.description.equals("null")) {

                System.out.println("Food: " + food);
                System.out.println("Name: " + food.name);
                System.out.println("Price: " + food.price);
                System.out.println("Description: " + food.description);

                return food;
            }
            foods.remove(i);
        }
        System.out.println("Returning NULL");
        return null;
    }

    /**
     * Helper method to extract image links from Bing response object
     *
     * @param input
     * @return
     */
    private List<String> getImages(String input) {
        List<String> images = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(input);
            JSONObject data = response.getJSONObject("d");
            JSONArray results = data.getJSONArray("results");

            for(int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String url = result.getString("MediaUrl");
                images.add(url);
            }
        }
        catch(JSONException e) {
            System.out.println(e);
        }

        return images;
    }
}