package com.netthreads.traffic;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.netthreads.traffic.view.NavigationDrawerFragment;
import com.netthreads.traffic.view.PreferencesHelper;
import com.netthreads.traffic.view.TrafficDataListFragment;

/**
 * Main Activity
 * <p/>
 * Note: Extends support class ActionBarActivity.
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String dataRegion;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // NOTE: Support
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Preferences
        // Apply default values for settings only once.
        PreferenceManager.setDefaultValues(this, R.xml.preferences_application, false);

        // -----------------------------------------------------------
        // Load filter preferences.
        // -----------------------------------------------------------
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean loadOnStartup = sharedPreferences.getBoolean(SettingsActivity.PREF_UPDATE_ON_START, SettingsActivity.DEFAULT_PREF_UPDATE_ON_START);
        boolean autoUpdate = sharedPreferences.getBoolean(SettingsActivity.PREF_AUTO_UPDATE, SettingsActivity.DEFAULT_PREF_AUTO_UPDATE);
    }

    /**
     * Check for Google Play services and direct user to play store if not found.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        // Check for google play services (which supplies google maps).
        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (connectionResult != ConnectionResult.SUCCESS)
        {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(connectionResult, this, -1);

            if (errorDialog != null)
            {
                errorDialog.show();
            }
        }
    }

    /**
     * On select item from navigation drawer.
     *
     * @param position   Selection index.
     * @param dataRegion Region selected.
     * @param dataUrl    Region url
     */
    @Override
    public void onNavigationDrawerItemSelected(int position, String dataRegion, String dataUrl)
    {
        this.dataRegion = dataRegion;

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, TrafficDataListFragment.newInstance(dataRegion, dataUrl))
                .commit();
    }

    /**
     * Handle refresh data.
     *
     * We check to see if the last loaded timestamp should be checked or not.
     *
     * @param force Don't check just load.
     */
    @Override
    public void refreshSelection(boolean force)
    {
        boolean refresh = true;
        if (!force)
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            long lastLoad = PreferencesHelper.getRegionLastLoaded(sharedPreferences, dataRegion);

            if (System.currentTimeMillis() - lastLoad < 10000)
            {
                refresh = false;
            }
        }

        if (refresh)
        {
            TrafficDataListFragment fragment = (TrafficDataListFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment != null)
            {
                fragment.refresh(null, null);
            }
        }
    }

    /**
     * On section attached handler.
     *
     * @param title
     */
    public void onSectionAttached(String title)
    {
        mTitle = title;
    }

    /**
     * Restore action bar.
     */
    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle options menu selections.
     *
     * @param item
     * @return True if handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId())
        {
            case (R.id.action_settings):
                // This intents has a return handler where we check to see if
                // something was changed.
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, SettingsActivity.ACTIVITY_ID);
                break;

            case R.id.action_refresh:
                refreshSelection(true);
                break;

            case R.id.action_map:
                Intent mapIntent = new Intent(this, MapActivity.class);
                mapIntent.putExtra(MapActivity.ARG_REGION, dataRegion);
                startActivity(mapIntent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle activity result.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // If settings request then check something was changed.
        if (requestCode == SettingsActivity.ACTIVITY_ID)
        {
            // If setting changed.
            if (resultCode == RESULT_OK)
            {
                // -----------------------------------------------------------
                // Load filter preferences.
                // -----------------------------------------------------------
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                boolean autoUpdate = sharedPreferences.getBoolean(SettingsActivity.PREF_AUTO_UPDATE, true);

                refreshSelection(true);
            }
        }
    }


}
