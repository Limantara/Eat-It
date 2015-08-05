package me.limantara.eatit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;

public class SetBudget extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private SeekBar seekBar;
    private TextView budget_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_budget);
        budget_value = (TextView) findViewById(R.id.budget_value);

        // Set up toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up drawer
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // Set up toolbar title
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(getResources().getStringArray(R.array.nav_drawer_labels)[3]);

        // Set up progress bar
        setUpSeekBar();
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
        getMenuInflater().inflate(R.menu.menu_set_budget, menu);
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
                intent = new Intent(this, RecentSuggestion.class);
                break;
            case 3:
                return;
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

    private void setUpSeekBar() {
        final SharedPreferences settings = getSharedPreferences("me.limantara.eatit", 0);
        final int offset = 7;
        int budgetPreference = settings.getInt("budget", AppController.DEFAULT_BUDGET_PREFERENCE);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(budgetPreference - offset);  System.out.println("budget preference: " + budgetPreference);
        budget_value.setText(String.valueOf(budgetPreference));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newBudget = offset + progress;
                budget_value.setText(String.valueOf(newBudget));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  System.out.println("stop called");
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("budget", seekBar.getProgress() + offset);
                editor.apply();
            }
        });
    }
}
