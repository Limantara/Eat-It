package me.limantara.eatit.fragment;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import me.limantara.eatit.API.BingAPI;
import me.limantara.eatit.API.BingAPI.BingListener;
import me.limantara.eatit.API.LocuAPI;
import me.limantara.eatit.API.LocuAPI.LocuListener;
import me.limantara.eatit.Helper.Util;
import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.model.Venue;
import me.limantara.eatit.view.CustomNetworkImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLatestSuggestion extends Fragment
    implements LocuListener, BingListener {

    private AppController app = AppController.getInstance();
    private Venue selectedVenue;

    private static final String DOWNLOADING_IMAGE = "Downloading image...";
    private static final Double ONE_MILE_IN_METER = 1609.34;

    private ImageView loadingIcon;
    private CustomNetworkImageView imageFood;
    private TextView foodName;
    private TextView foodPrice;
    private TextView foodDescription;
    private TextView restaurantName;
    private TextView restaurantDistance;
    private Button direction;

    public FragmentLatestSuggestion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocuAPI(this, app.getPreferredDistance()).makeApiCall();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_latest_suggestion, container, false);
        loadingIcon = (ImageView) root.findViewById(R.id.loadingIcon);
        imageFood = (CustomNetworkImageView) root.findViewById(R.id.imageFood);
        foodName = (TextView) root.findViewById(R.id.foodName);
        foodPrice = (TextView) root.findViewById(R.id.foodPrice);
        foodDescription = (TextView) root.findViewById(R.id.foodDescription);
        restaurantName = (TextView) root.findViewById(R.id.restaurantName);
        restaurantDistance = (TextView) root.findViewById(R.id.restaurantDistance);
        direction = (Button) root.findViewById(R.id.direction);
        animateLoading();
        hideFoodInfo();

        return root;
    }

    @Override
    public void onLocuResponse(List<Venue> venueList) {
        selectedVenue = pickAndRemoveAVenue(venueList);
        restaurantName.setText(DOWNLOADING_IMAGE);
        new BingAPI(this, selectedVenue.getFoods(app.getPreferredBudget())).makeApiCall();
    }

    @Override
    public void onLocuErrorResponse() {
        loadingIcon.clearAnimation();
        System.out.println("Error Locu");
    }

    @Override
    public void onBingResponse(Venue.Item food) {
        fillFoodInfo(food);
    }

    @Override
    public void onBingErrorResponse() {

    }

    /**
     * Get a random venue and remove it from the list.
     *
     * @return
     */
    private Venue pickAndRemoveAVenue(List<Venue> venueList) {
        Venue venue = null;
        int budget = app.getPreferredBudget();

        while( ! venueList.isEmpty()) {
            int i = Util.getRandomNumber(0, venueList.size());
            venue = venueList.get(i);
            venueList.remove(i);

            if( ! venue.getFoods(budget).isEmpty()) {
                System.out.println("foods: " + venue.getFoods(budget));
                break;
            }
        }

        return venue;
    }

    /**
     * Helper method to animate the loading icon
     */
    private void animateLoading() {
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(3000);

        loadingIcon.startAnimation(anim);
    }

    /**
     * Helper method to animate the loading icon into finishing state
     */
    private void animateFinishing(final ImageView view) {
        Integer colorFrom = getResources().getColor(R.color.divider);
        Integer colorTo = getResources().getColor(R.color.accent);

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setColorFilter((Integer) animator.getAnimatedValue());
            }
        });

        Animation blink = new AlphaAnimation(1, 0);
        blink.setDuration(1000);
        blink.setInterpolator(new LinearInterpolator());
        blink.setRepeatCount(Animation.INFINITE);

        view.startAnimation(blink);
        colorAnimator.start();
        view.clearAnimation();
    }

    /**
     * Helper method to dismiss food information
     */
    private void hideFoodInfo() {
        imageFood.setVisibility(View.GONE);
        foodName.setVisibility(View.GONE);
        foodPrice.setVisibility(View.GONE);
        foodDescription.setVisibility(View.GONE);
        direction.setVisibility(View.GONE);
    }

    private void fillFoodInfo(Venue.Item food) {
        foodName.setText(food.name);
        foodPrice.setText("$ " + food.price);
        foodDescription.setText(food.description);
        fillImage(food);
        showFoodInfo();
    }

    private void fillRestaurantInfo() {
        restaurantName.setText(selectedVenue.name);
        restaurantDistance.setText(calculateDistanceTo(selectedVenue));
    }

    /**
     * Helper method to populate food's image
     */
    private void fillImage(Venue.Item food) {
        String url = food.images.get(0);

        ImageRequest imageRequest = new ImageRequest(url,
            new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imageFood.setLocalImageBitmap(response);
                    animateFinishing(loadingIcon);
                    fillRestaurantInfo();
                }
            }, 0, 0, null,
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error image");
                }
            }
        );

        app.addToRequestQueue(imageRequest);
    }

    /**
     * Helper method to set food info to visible
     */
    private void showFoodInfo() {
        imageFood.setVisibility(View.VISIBLE);
        foodName.setVisibility(View.VISIBLE);
        foodPrice.setVisibility(View.VISIBLE);
        foodDescription.setVisibility(View.VISIBLE);
        direction.setVisibility(View.VISIBLE);
    }

    /**
     * Helper method to calculate distance to the restaurant.
     *
     * @param venue
     * @return
     */
    private String calculateDistanceTo(Venue venue) {
        Location destination = new Location("");
        destination.setLatitude(venue.getLatitude());
        destination.setLongitude(venue.getLongitude());

        Location source = new Location("");
        source.setLatitude(AppController.getInstance().getLatitude());
        source.setLongitude(AppController.getInstance().getLongitude());

        Float distanceMeter = source.distanceTo(destination);
        Float distanceMiles = new Float(distanceMeter / ONE_MILE_IN_METER);

        return String.format("%.2g miles", distanceMiles);
    }
}
