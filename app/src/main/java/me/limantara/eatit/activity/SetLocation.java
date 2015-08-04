package me.limantara.eatit.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;

public class SetLocation extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private TextView cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        // Set up toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up drawer
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // Set up city name
        cityName= (TextView) findViewById(R.id.city_name);

        try {
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(AppController.getLatitude(), AppController.getLongitude(), 1);

            if (addresses.size() > 0)
                cityName.setText(addresses.get(0).getLocality());
        }
        catch(IOException e) {
            cityName.setText("Unknown location");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_location, menu);
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
            case 1:
                intent = new Intent(this, DisplayResult.class);
                break;
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
                return;
            case 6:
                intent = new Intent(this, HelpCenter.class);
                break;
            default:
                return;
        }

        startActivity(intent);
    }
}
