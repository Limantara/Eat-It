package me.limantara.eatit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

import me.limantara.eatit.API.BingAPI;
import me.limantara.eatit.API.LocuAPI;
import me.limantara.eatit.Helper.SQLiteHelper;
import me.limantara.eatit.Helper.TimeHelper;
import me.limantara.eatit.Helper.Util;
import me.limantara.eatit.R;
import me.limantara.eatit.model.Venue;

public class MainActivity extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    public final static String TOOLBAR_TITLE = "me.limantara.eatit.TOOLBAR_TITLE";
    public final static String VENUE = "me.limantara.eatitorleaveit.VENUE";
    public final static String FOOD = "me.limantara.eatitorleaveit.FOOD";
    public final static String STORE_FOOD = "me.limantara.eatitorleaveit.STORE_FOOD";

    private List<Venue> venues;
    private Venue selectedVenue;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        final FloatingActionButton buttonExplore =
                (FloatingActionButton) findViewById(R.id.buttonExplore);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        animation.setStartOffset(500);
        buttonExplore.startAnimation(animation);

        // Set up button click listener
//        buttonExplore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            Intent intent = new Intent(MainActivity.this, DisplayResult.class);
//            startActivity(intent);
//            }
//        });
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
        String[] titles = getResources().getStringArray(R.array.nav_drawer_labels);

        if(position > 0) {
            Intent intent = new Intent(MainActivity.this, DisplayResult.class);
            intent.putExtra(TOOLBAR_TITLE, titles[position]);
            startActivity(intent);
        }
    }
    /**
     * Button click listener to make an API call to Locu.com
     *
     * @param view
     */
    public void callLocu(View view) {
        int radius = 4828; // 3 miles
        new LocuAPI(this, radius).makeApiCall();
    }

    /**
     * Select a random food from a list of restaurants and
     * find its pictures on Bing
     *
     * @param venues
     */
    public void findFood(List<Venue> venues) {
        this.venues = venues;
        selectedVenue = pickAVenue();
        new BingAPI(this, selectedVenue.getFoods()).makeApiCall();
    }

    /**
     * Find another venue if none of the food in this place has
     * a picture on Bing.
     */
    public void findAnotherVenue() { System.out.println("Find another venue is called");
        findFood(venues);
    }

    /**
     * Move on to the next activity - displaying the food result
     */
    public void moveOn(Venue.Item selectedFood, boolean storeFood) {
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

        while( ! venues.isEmpty()) {
            int i = Util.getRandomNumber(0, venues.size());
            venue = venues.get(i);
            venues.remove(i);

            if(venue.getFoods() != null)
                break;
        }

        return venue;
    }

    /**
     * Check if the limit (one suggestion per eat time) has been reached
     *
     * @return
     */
    private boolean checkLimit() {
        SQLiteHelper db = SQLiteHelper.getInstance(this); db.refresh();
        Venue.Item latestFood = db.getLatestFood();
        Long current_eat_time = TimeHelper.getEatTimeObject().getTimeInMillis();

        if(latestFood != null)
            return latestFood.created_at >= current_eat_time;
        else
            return false;
    }
}
