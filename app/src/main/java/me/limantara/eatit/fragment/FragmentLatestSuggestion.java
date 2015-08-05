package me.limantara.eatit.fragment;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
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

import java.util.List;

import me.limantara.eatit.API.LocuAPI;
import me.limantara.eatit.API.LocuAPI.LocuListener;
import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.model.Venue;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLatestSuggestion extends Fragment
    implements LocuListener {

    private AppController app = AppController.getInstance();

    private ImageView loadingIcon;
    private ImageView imageFood;
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
        imageFood = (ImageView) root.findViewById(R.id.imageFood);
        foodName = (TextView) root.findViewById(R.id.foodName);
        foodPrice = (TextView) root.findViewById(R.id.foodPrice);
        foodDescription = (TextView) root.findViewById(R.id.foodDescription);
        restaurantName = (TextView) root.findViewById(R.id.restaurantName);
        restaurantDistance = (TextView) root.findViewById(R.id.restaurantDistance);
        direction = (Button) root.findViewById(R.id.direction);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        animateLoading();
        hideFoodInfo();
    }


    @Override
    public void onLocuResponse(List<Venue> venueList) {
        animateFinishing(loadingIcon);

        fillFoodInfo();
        showFoodInfo();

        restaurantName.setText(venueList.get(0).name);
        restaurantDistance.setText("3 miles");
    }

    @Override
    public void onLocuErrorResponse() {
        loadingIcon.clearAnimation();
        System.out.println("Error Locu");
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

    private void fillFoodInfo() {
        foodName.setText("FoodName");
        foodPrice.setText("FoodPrice");
        foodDescription.setText("FoodDescription");
    }

    private void showFoodInfo() {
        imageFood.setVisibility(View.VISIBLE);
        foodName.setVisibility(View.VISIBLE);
        foodPrice.setVisibility(View.VISIBLE);
        foodDescription.setVisibility(View.VISIBLE);
        direction.setVisibility(View.VISIBLE);
    }
}
