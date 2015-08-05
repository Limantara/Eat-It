package me.limantara.eatit.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import me.limantara.eatit.Helper.SQLiteHelper;
import me.limantara.eatit.R;
import me.limantara.eatit.adapter.RecentSuggestionAdapter;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.model.Venue;

public class RecentSuggestion extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private RecyclerView foodList;
    private List<Venue.Item> foods;

    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_suggestion);
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
        toolbarTitle.setText(getResources().getStringArray(R.array.nav_drawer_labels)[2]);

        foodList = (RecyclerView) findViewById(R.id.foodList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        foodList.setLayoutManager(layoutManager);

        foods = dbHelper.getAllFoods();

        if(foods.isEmpty()) {
            foodList.setVisibility(View.GONE);
            TextView emptyText = (TextView) findViewById(R.id.emptyText);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            foodList.setLayoutManager(new LinearLayoutManager(this));
            foodList.setAdapter(new RecentSuggestionAdapter(foods));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        foodList.setVisibility(View.VISIBLE);
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
        getMenuInflater().inflate(R.menu.menu_recent_suggestion, menu);
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
                intent = new Intent(this, DisplayResult.class);
                break;
            case 2:
                return;
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

    public void launchGoogleMaps(View view) {
        int pos = foodList.getChildPosition((View) view.getParent().getParent());
        Venue.Item food = foods.get(pos);
        Venue venue = dbHelper.findVenueFromFood(food);

        Float sLatitude = AppController.getInstance().getLatitude();
        Float sLongitude = AppController.getInstance().getLongitude();
        Float dLatitude = venue.getLatitude();
        Float dLongitude = venue.getLongitude();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s", sLatitude, sLongitude, dLatitude, dLongitude)));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}
