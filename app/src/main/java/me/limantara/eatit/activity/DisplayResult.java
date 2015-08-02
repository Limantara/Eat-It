package me.limantara.eatit.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import me.limantara.eatit.Helper.SQLiteHelper;
import me.limantara.eatit.Helper.VolleyHelper;
import me.limantara.eatit.R;
import me.limantara.eatit.model.Venue;

public class DisplayResult extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private Venue selectedVenue;
    private Venue.Item selectedFood;
    private List<String> images;

    private TextView restaurantName;
    private TextView foodName;
    private TextView foodPrice;
    private TextView foodDescription;
    private ImageView imageFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

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

        Intent intent = getIntent();
        if(intent.hasExtra(MainActivity.VENUE) && intent.hasExtra(MainActivity.FOOD)) {
            selectedFood = (Venue.Item) intent.getSerializableExtra(MainActivity.FOOD);
            selectedVenue = (Venue) intent.getSerializableExtra(MainActivity.VENUE);
        }
        else {
            SQLiteHelper dbHelper = SQLiteHelper.getInstance(this);
            selectedFood = dbHelper.getLatestFood();
            selectedVenue = dbHelper.findVenueFromFood(selectedFood);
        }

        fillTextViews();
        fillImage();
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
                startActivity(intent);
                break;
            case 1:
                break;
            case 2:
                intent = new Intent(this, RecentSuggestion.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Helper method to populate text views
     */
    private void fillTextViews() {
        restaurantName = (TextView) findViewById(R.id.restaurantName);
        foodName = (TextView) findViewById(R.id.foodName);
        foodPrice = (TextView) findViewById(R.id.foodDescription);
        foodDescription = (TextView) findViewById(R.id.foodDescription);
        imageFood = (ImageView) findViewById(R.id.imageFood);

        restaurantName.setText(selectedVenue.name);
        foodName.setText(selectedFood.name);
        foodPrice.setText("$ " + selectedFood.price);
        foodDescription.setText(selectedFood.description);
    }

    /**
     * Helper method to populate food' image
     */
    private void fillImage() {
        images = selectedFood.images;
        final String url = images.get(0);
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageFood.setImageBitmap(bitmap);

                        Intent intent = DisplayResult.this.getIntent();
                        // save to SQLite if store food is true
                        if(intent.hasExtra(MainActivity.STORE_FOOD) &&
                                intent.getExtras().getBoolean(MainActivity.STORE_FOOD)) {
                            SQLiteHelper dbHelper = SQLiteHelper.getInstance(DisplayResult.this);
                            dbHelper.createFood(selectedFood, selectedVenue,  url);
                            dbHelper.createVenue(selectedVenue);
                            dbHelper.printAll();
                        }
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError);

                        if( ! images.isEmpty()) {
                            DisplayResult.this.images.remove(0);
                            DisplayResult.this.fillImage();
                        }
                    }
                }
        );

        VolleyHelper.getInstance(this).addToRequestObject(request);
    }
}
