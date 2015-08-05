package me.limantara.eatit.activity;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import me.limantara.eatit.Helper.SQLiteHelper;
import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.model.Venue;

public class DisplayResult extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private Venue selectedVenue;
    private Venue.Item selectedFood;
    private List<String> images;

    private CardView foodCard;
    private TextView restaurantName;
    private TextView restaurantDistance;
    private TextView foodName;
    private TextView foodPrice;
    private TextView foodDescription;
    private ImageView imageFood;

    private SQLiteHelper dbHelper;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);
        dbHelper = SQLiteHelper.getInstance(this);

        // Set up toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up drawer
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(
                R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // Set up toolbar title
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(getResources().getStringArray(R.array.nav_drawer_labels)[1]);

        intent = getIntent();

//        if(intent.hasExtra(MainActivity.VENUE) && intent.hasExtra(MainActivity.FOOD)) {
//            selectedFood = (Venue.Item) intent.getSerializableExtra(MainActivity.FOOD);
//            selectedVenue = (Venue) intent.getSerializableExtra(MainActivity.VENUE);
//        }
//        else {
//            selectedFood = dbHelper.getLatestFood();
//
//            if(selectedFood != null) {
//                selectedVenue = dbHelper.findVenueFromFood(selectedFood);
//            }
//            else {
//                showEmptyMessage();
//                return;
//            }
//        }

        fillTextViews();
        fillImage();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(foodCard != null)
            foodCard.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.RootView));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null)
            view.getBackground().setCallback(null);

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            for (int i = 0; i < viewGroup.getChildCount(); i++)
                unbindDrawables(viewGroup.getChildAt(i));

            if (!(view instanceof AdapterView) && !(view instanceof RecyclerView))
                viewGroup.removeAllViews();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_result, menu);
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
                intent = new Intent(this, MainActivity.class);
                break;
            case 1:
                return;
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
        finish();
    }

    /**
     * Helper method to populate text views
     */
    private void fillTextViews() {
        restaurantName = (TextView) findViewById(R.id.restaurantName);
        restaurantDistance = (TextView) findViewById(R.id.restaurantDistance);
        foodName = (TextView) findViewById(R.id.foodName);
        foodPrice = (TextView) findViewById(R.id.foodPrice);
        foodDescription = (TextView) findViewById(R.id.foodDescription);
        imageFood = (ImageView) findViewById(R.id.imageFood);

        restaurantName.setText(selectedVenue.name);
        restaurantDistance.setText(calculateDistanceTo(selectedVenue));
        foodName.setText(selectedFood.name);
        foodPrice.setText("$ " + selectedFood.price);
        foodDescription.setText(selectedFood.description);
    }

    /**
     * Helper method to populate food' image
     */
    private void fillImage() {
        images = selectedFood.images;
        String url = images.get(0);

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView imageFood = (NetworkImageView) findViewById(R.id.imageFood);
        imageFood.setImageUrl(url, imageLoader);

//        if(intent.hasExtra(MainActivity.STORE_FOOD) && intent.getBooleanExtra(MainActivity.STORE_FOOD, false)) {
//            dbHelper.createFood(selectedFood, selectedVenue, url);
//        }
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
        source.setLatitude(AppController.getLatitude());
        source.setLongitude(AppController.getLongitude());

        Float distanceMeter = source.distanceTo(destination);
        Float distanceMiles = new Float(distanceMeter / 1609.34);

        return String.format("%.2g miles", distanceMiles);
    }

    private void showEmptyMessage() {
        foodCard = (CardView) findViewById(R.id.foodCard);
        foodCard.setVisibility(View.GONE);

        LinearLayout parent = (LinearLayout) foodCard.getParent();
        TextView emptyText = new TextView(this);

        // style
        emptyText.setHeight(72);
        emptyText.setGravity(Gravity.CENTER);

        emptyText.setText("You don't have any suggestion yet");
        parent.addView(emptyText);
    }

    public void launchGoogleMaps(View view) {

        Float sLatitude = AppController.getLatitude();
        Float sLongitude = AppController.getLongitude();
        Float dLatitude = selectedVenue.getLatitude();
        Float dLongitude = selectedVenue.getLongitude();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s", sLatitude, sLongitude, dLatitude, dLongitude)));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}
