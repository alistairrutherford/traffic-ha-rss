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

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.netthreads.traffic.R;
import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.provider.TrafficDataRecordProvider;
import com.netthreads.traffic.visitor.AddMarkerVisitor;
import com.netthreads.traffic.visitor.CalculateBoundsVisitor;
import com.netthreads.traffic.visitor.CursorVisitor;

/**
 * Map Fragment
 * <p/>
 * Will populate map with data from specified region and optionally centre and zoom to supplied point.
 * <p/>
 * You will notice there is a lot of faffing around trying to get the view bounds before the view is
 * rendered. We don't want any flipping around when the map is drawn.
 * <p/>
 * Also, 'visitors'. Well, your mileage may vary. I like em.
 */
public class TrafficDataMapFragment extends Fragment implements OnMapReadyCallback
{
    public static final String ARG_REGION = "region";
    public static final String ARG_LAT    = "lat";
    public static final String ARG_LNG    = "lng";

    private int SINGLE_MARKER_ZOOM = 13;
    private int MAP_VIEW_BORDER    = 20;

    private SupportMapFragment mapFragment;
    private GoogleMap          map;

    private String[] SELECT_REGIONS = {""};
    private String   WHERE_REGION   = TrafficRecord.TEXT_REGION + "= ?";

    /**
     * Construct fragment.
     */
    public TrafficDataMapFragment()
    {
    }

    /**
     * Create fragment instance.
     *
     * @param region
     * @param data
     *
     * @return The fragment instance.
     */
    public static final TrafficDataMapFragment newInstance(String region, TrafficRecord data)
    {
        Bundle bundle = new Bundle();

        bundle.putString(TrafficDataMapFragment.ARG_REGION, region);

        if (data != null)
        {
            bundle.putString(TrafficDataMapFragment.ARG_LAT, data.getLatitude());
            bundle.putString(TrafficDataMapFragment.ARG_LNG, data.getLongitude());
        }

        TrafficDataMapFragment fragment = new TrafficDataMapFragment();

        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * On Create View.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return The view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        this.map = mapFragment.getMap();

        setupView(rootView);

        return rootView;
    }

    /**
     * On Map Ready.
     *
     * @param map
     */
    @Override
    public void onMapReady(final GoogleMap map)
    {
        Bundle bundle = getArguments();

        // Get Data
        String region = bundle.getString(ARG_REGION);

        // Draw all markers
        populateMap(region);
    }

    /**
     * Setup view.
     *
     * @param mapView
     */
    private void setupView(final View mapView)
    {
        Bundle bundle = getArguments();

        String region = bundle.getString(ARG_REGION);
        final String lat = bundle.getString(ARG_LAT);
        final String lng = bundle.getString(ARG_LNG);

        // Load region and generate view bounds.
        final LatLngBounds bounds = calculateBounds(region);

        if (mapView.getViewTreeObserver().isAlive())
        {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener()
                    {
                        @SuppressLint("NewApi") // We check which build version we are using.
                        @Override
                        public void onGlobalLayout()
                        {
                            // Add listener.
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                            {
                                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                            else
                            {
                                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }

                            // Centre if directed to.
                            if (lat != null && lng != null)
                            {
                                Double latitude = Double.parseDouble(lat);
                                Double longitude = Double.parseDouble(lng);

                                LatLng location = new LatLng(latitude.doubleValue(), longitude.doubleValue());

                                map.moveCamera(CameraUpdateFactory.zoomTo(SINGLE_MARKER_ZOOM));

                                map.moveCamera(CameraUpdateFactory.newLatLng(location));
                            }
                            else
                            {
                                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_VIEW_BORDER));
                            }
                        }
                    });
        }

    }

    /**
     * Calculate data bounds.
     *
     * @param region
     * @return The view bounds.
     */
    private LatLngBounds calculateBounds(String region)
    {
        CalculateBoundsVisitor visitor = new CalculateBoundsVisitor();

        visitData(region, visitor);

        LatLngBounds bounds = visitor.getLatLngBounds();

        return bounds;
    }

    /**
     * Populate map with markers.
     *
     * @param region
     */
    private void populateMap(String region)
    {
        AddMarkerVisitor visitor = new AddMarkerVisitor(map);

        visitData(region, visitor);
    }

    /**
     * Process data using defined visitor.
     *
     * @param region
     * @param visitor
     */
    private void visitData(String region, CursorVisitor visitor)
    {
        Cursor cursor = null;
        int itemCount = 0;

        try
        {
            SELECT_REGIONS[0] = region;
            cursor = getActivity().getContentResolver().query(TrafficDataRecordProvider.CONTENT_URI,
                    TrafficDataListFragment.PROJECTION,
                    WHERE_REGION,
                    SELECT_REGIONS,
                    null);

            itemCount = cursor.getCount();

            if (itemCount > 0)
            {
                cursor.moveToFirst();

                while (!cursor.isAfterLast())
                {
                    visitor.visit(cursor);

                    cursor.moveToNext();
                }
            }

        }
        catch (Throwable t)
        {
            Log.e("visitData", t.getLocalizedMessage());
        }
        finally
        {
            if (cursor != null && (itemCount > 0))
            {
                cursor.close();
            }
        }
    }

}