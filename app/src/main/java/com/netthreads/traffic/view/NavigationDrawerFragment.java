package com.netthreads.traffic.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.netthreads.rss.OpmlData;
import com.netthreads.traffic.R;
import com.netthreads.traffic.helper.PreferencesHelper;
import com.netthreads.traffic.loader.TrafficOpmlLoader;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment
{

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks navigationDrawerCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private DrawerLayout drawerLayout;
    private ListView listView;
    private View view;

    private int currentSelectedPosition = 0;
    private boolean fromSavedInstanceState;
    private boolean userLearnedDrawer;

    private String[]            selectionTitles;
    private Map<String, String> dataMap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userLearnedDrawer = sharedPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null)
        {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            fromSavedInstanceState = true;
        }

        // Get data details
        dataMap = buildDataMap(getActivity().getApplicationContext());

        // Build section titles.
        selectionTitles = buildSectionTitles(dataMap);

        // We are going to persist our last loaded date times in preferences.
        PreferencesHelper.initialiseRegionLastLoaded(sharedPreferences, selectionTitles);

        // Select either the default item (0) or the last selected item.
        selectItem(currentSelectedPosition);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    /**
     * Create navigation drawer view.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     *
     * @return The view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        listView = (ListView) rootView.findViewById(R.id.navigation_drawer_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectItem(position);
            }
        });

        ActionBar actionBar = getActionBar();
        Context themedContext = actionBar.getThemedContext();

        // Build navigation menu list adapter with selectionTitles
        listView.setAdapter(new ArrayAdapter<String>(
                themedContext,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                selectionTitles));

        listView.setItemChecked(currentSelectedPosition, true);

        return rootView;
    }

    public boolean isDrawerOpen()
    {
        return drawerLayout != null && drawerLayout.isDrawerOpen(view);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout)
    {
        view = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                NavigationDrawerFragment.this.drawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        )
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                if (!isAdded())
                {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                if (!isAdded())
                {
                    return;
                }

                if (!userLearnedDrawer)
                {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    userLearnedDrawer = true;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sharedPreferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!userLearnedDrawer && !fromSavedInstanceState)
        {
            this.drawerLayout.openDrawer(view);
        }

        // Defer code dependent on restoration of previous instance state.
        this.drawerLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                actionBarDrawerToggle.syncState();
            }
        });

        this.drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    /**
     * Select item on list.
     *
     * @param position
     */
    private void selectItem(int position)
    {
        currentSelectedPosition = position;

        if (listView != null)
        {
            listView.setItemChecked(position, true);
        }

        if (drawerLayout != null)
        {
            drawerLayout.closeDrawer(view);
        }

        if (navigationDrawerCallbacks != null)
        {
            String title = selectionTitles[position];
            String url = dataMap.get(title);

            navigationDrawerCallbacks.onNavigationDrawerItemSelected(position, title, url);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            navigationDrawerCallbacks = (NavigationDrawerCallbacks) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        navigationDrawerCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (drawerLayout != null && isDrawerOpen())
        {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Handle action bar menu items.
     *
     * @param item
     * @return True if handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    /**
     * Return action bar.
     *
     * @return
     */
    private ActionBar getActionBar()
    {
        Activity activity = getActivity();

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();

        return actionBar;
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks
    {
        /**
         * Called when an item in the navigation drawer is selected.
         *
         * @param position
         * @param dataRegion
         * @param dataUrl
         */
        void onNavigationDrawerItemSelected(int position, String dataRegion, String dataUrl);

        /**
         * Refresh data in view.
         */
        void refreshSelection(boolean force);
    }

    /**
     * Build list of selectionTitles from the oplm data.
     *
     * @param context
     * @return String list of selectionTitles.
     */
    private Map<String, String> buildDataMap(Context context)
    {
        Map<String, String> map = new LinkedHashMap<>();

        try
        {
            // Build regions list
            TrafficOpmlLoader trafficOpmlLoader = new TrafficOpmlLoader(context);

            List<OpmlData> data = trafficOpmlLoader.loadOpml();

            if (!data.isEmpty())
            {
                for (OpmlData opmlData : data)
                {
                    map.put(opmlData.getTitle(), opmlData.getXmlUrl());
                }
            }
        }
        catch (IOException e)
        {
            Log.e(this.getClass().getCanonicalName(), e.getLocalizedMessage());
        }
        catch (XmlPullParserException e)
        {
            Log.e(this.getClass().getCanonicalName(), e.getLocalizedMessage());
        }

        return map;
    }

    /**
     * Build section titles for menu.
     *
     * @param map
     * @return Array of title string.
     */
    private String[] buildSectionTitles(Map<String, String> map)
    {
        Set<String> keys = map.keySet();

        String[] values = new String[keys.size()];
        int index = 0;
        for (String key : keys)
        {
            values[index++] = key;
        }

        return values;
    }
}
