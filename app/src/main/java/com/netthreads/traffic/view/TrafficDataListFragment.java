/*
 * *
 *  * -----------------------------------------------------------------------
 *  * Copyright 2015 - Alistair Rutherford - www.netthreads.co.uk
 *  * -----------------------------------------------------------------------
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.netthreads.traffic.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netthreads.traffic.DetailsActivity;
import com.netthreads.traffic.MainActivity;
import com.netthreads.traffic.MapActivity;
import com.netthreads.traffic.R;
import com.netthreads.traffic.defaults.Defaults;
import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.helper.PreferencesHelper;
import com.netthreads.traffic.loader.TrafficRssLoader;
import com.netthreads.traffic.provider.TrafficDataRecordProvider;

import org.xmlpull.v1.XmlPullParserException;

/**
 * -------------------------------------------------------------------
 * Test Data List Fragment.
 * -------------------------------------------------------------------
 */
public class TrafficDataListFragment extends Fragment implements IItemClickListener
{
    /**
     * Columns mapped for display of object.
     */
    public static final String[] PROJECTION = new String[]
            {
                    TrafficRecord.TEXT_CATEGORY_CLASS,
                    TrafficRecord.TEXT_CATEGORY,
                    TrafficRecord.TEXT_SEVERITY,
                    TrafficRecord.TEXT_ROAD,
                    TrafficRecord.TEXT_REGION,
                    TrafficRecord.TEXT_TITLE,
                    TrafficRecord.TEXT_DESCRIPTION,
                    TrafficRecord.TEXT_LATITUDE,
                    TrafficRecord.TEXT_LONGITUDE,
                    TrafficRecord.TEXT_EVENT_END,
                    TrafficRecord.TEXT_EVENT_START,
                    TrafficRecord._ID
            };

    private String[] SELECT_REGIONS = {""};
    private String   WHERE_REGION   = TrafficRecord.TEXT_REGION + "= ?";

    /**
     * The fragment arguments.
     */
    private static final String ARG_DATA_REGION = "data_region";
    private static final String ARG_DATA_URL    = "data_url";

    private RecyclerView               recyclerView;
    private TrafficDataCursorAdapter   adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String lastUrl;
    private String lastRegion;
    private boolean loading = false;

    // -------------------------------------------------------------------
    // Loader call backs
    // -------------------------------------------------------------------
    protected LoaderManager.LoaderCallbacks<Object> dataloaderCallbacks = new LoaderManager.LoaderCallbacks<Object>()
    {
        @Override
        public Loader<Object> onCreateLoader(int id, Bundle args)
        {
            TrafficRssLoader trafficDataLoader = null;

            ActionBarActivity activity = (ActionBarActivity) getActivity();

            // NOTE: Support
            activity.setSupportProgressBarIndeterminate(true);
            activity.setSupportProgressBarVisibility(true);

            String url = args.getString(ARG_DATA_URL);
            String region = args.getString(ARG_DATA_REGION);


            // If we are just refreshing then the url will be passed as null to indicate use the existing value.
            if (region == null || region.isEmpty())
            {
                region = lastRegion;
            }

            if (url == null || url.isEmpty())
            {
                url = lastUrl;
            }

            try
            {
                trafficDataLoader = new TrafficRssLoader(getActivity(), TrafficDataRecordProvider.CONTENT_URI);

                trafficDataLoader.setUrl(url);
                trafficDataLoader.setRegion(region);
            }
            catch (XmlPullParserException e)
            {
                Log.e("onCreateLoader", e.getLocalizedMessage());
            }

            return trafficDataLoader;
        }

        /**
         * Handle on finish load.
         *
         * @param loader
         * @param data
         */
        @Override
        public void onLoadFinished(Loader<Object> loader, Object data)
        {
            ActionBarActivity activity = (ActionBarActivity) getActivity();

            // NOTE: Support
            activity.setSupportProgressBarIndeterminate(false);
            activity.setSupportProgressBarVisibility(false);

            // On data loaded get a cursor into the new data and swap into into the list view.
            SELECT_REGIONS[0] = lastRegion;
            Cursor cursor = getActivity().getContentResolver().query(TrafficDataRecordProvider.CONTENT_URI,
                    TrafficDataListFragment.PROJECTION,
                    WHERE_REGION,
                    SELECT_REGIONS,
                    null);

            adapter.swapCursor(cursor);

            setLoading(false);
        }

        /**
         * Reset loader.
         *
         * @param loader
         */
        @Override
        public void onLoaderReset(Loader<Object> loader)
        {
            // Reset
            setLoading(false);
        }
    };

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     *
     * @param dataRegion
     * @return New fragment
     */
    public static final TrafficDataListFragment newInstance(String dataRegion, String dataUrl)
    {
        TrafficDataListFragment fragment = new TrafficDataListFragment();

        Bundle args = new Bundle();

        args.putString(ARG_DATA_URL, dataUrl);
        args.putString(ARG_DATA_REGION, dataRegion);

        fragment.setArguments(args);

        return fragment;
    }

    /**
     * On attach to parent activity.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        Bundle arguments = getArguments();

        String dataRegion = arguments.getString(ARG_DATA_REGION);
        String dataUrl = arguments.getString(ARG_DATA_URL);

        // Set up activity title
        ((MainActivity) activity).onSectionAttached(dataRegion);

        lastRegion = dataRegion;
        lastUrl = dataUrl;

        // Load data
        refresh(dataUrl, dataRegion, false);
    }

    /**
     * Refresh data in view by restarting loader.
     *
     * @param url    Selected data url.
     * @param region Selected data region.
     * @param force
     */
    public void refresh(String url, String region, boolean force)
    {
        boolean refresh = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        long timestamp = System.currentTimeMillis();

        // If we are not forcing a refresh of the data then check to see if enough time has elapsed to fetch the data.
        if (!force)
        {
            long lastLoad = PreferencesHelper.getRegionLastLoaded(sharedPreferences, lastRegion);

            long difference = timestamp - lastLoad;

            if (difference < Defaults.REFRESH_TIMEOUT_MSEC)
            {
                refresh = false;
            }
        }

        if (refresh)
        {
            setLoading(true);

            Bundle bundle = new Bundle();
            bundle.putString(ARG_DATA_URL, url);
            bundle.putString(ARG_DATA_REGION, region);

            getLoaderManager().restartLoader(TrafficRssLoader.LOADER_ID, bundle, dataloaderCallbacks).forceLoad();

            PreferencesHelper.setRegionLastLoaded(sharedPreferences, region, timestamp);
        }
    }

    /**
     * On create view.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return The view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.data_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        recyclerView.setClickable(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Specify an adapter
        adapter = new TrafficDataCursorAdapter(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    /**
     * Click handler.
     *
     * @param record
     */
    @Override
    public void onClick(TrafficRecord record)
    {
        // Build intent and add data.
        Intent detailIntent = new Intent(getActivity(), DetailsActivity.class);
        detailIntent.putExtra(DetailsActivity.ARG_ITEM, record);

        // Launch from intent
        startActivity(detailIntent);
    }


    /**
     * On long click handler.
     *
     * @param record
     */
    @Override
    public void onLongClick(TrafficRecord record)
    {
        // Build intent and add data.
        Intent mapIntent = new Intent(getActivity(), MapActivity.class);
        mapIntent.putExtra(MapActivity.ARG_REGION, lastRegion);
        mapIntent.putExtra(MapActivity.ARG_ITEM, record);

        // Launch from intent
        startActivity(mapIntent);
    }

    /**
     * Return loading flag.
     *
     * @return The loading flag
     */
    public synchronized boolean isLoading()
    {
        return loading;
    }

    /**
     * Set loading flag.
     *
     * @param loading
     */
    public synchronized void setLoading(boolean loading)
    {
        this.loading = loading;
    }
}