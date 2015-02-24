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
import com.google.android.gms.maps.model.MarkerOptions;
import com.netthreads.traffic.R;
import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.provider.TrafficDataRecordProvider;
import com.netthreads.traffic.visitor.AddMarkerVisitor;
import com.netthreads.traffic.visitor.CalculateBoundsVisitor;
import com.netthreads.traffic.visitor.MapVisitor;

/**
 * Map Fragment
 * <p/>
 * Will populate map with data from specified region and optionally centre and zoom to supplied point.
 */
public class TrafficDataMapFragment extends Fragment implements OnMapReadyCallback
{
    public static final String ARG_REGION = "region";
    public static final String ARG_LAT = "lat";
    public static final String ARG_LNG = "lng";

    private SupportMapFragment mapFragment;
    private GoogleMap map;

    private String[] SELECT_REGIONS = {""};
    private String WHERE_REGION = TrafficRecord.TEXT_REGION + "= ?";

    /**
     * Construct fragment.
     */
    public TrafficDataMapFragment()
    {
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

        populateView(rootView);

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
        final String lat = bundle.getString(ARG_LAT);
        final String lng = bundle.getString(ARG_LNG);

        // Draw all markers
        populateMap(region);
    }

    /**
     * Populate view.
     *
     * @param mapView
     */
    private void populateView(final View mapView)
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
                            // TODO populate map
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                            {
                                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else
                            {
                                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }

                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

                            if (lat != null && lng != null)
                            {
                                Double latitude = Double.parseDouble(lat);
                                Double longitude = Double.parseDouble(lng);

                                LatLng location = new LatLng(latitude.doubleValue(), longitude.doubleValue());

                                map.moveCamera(CameraUpdateFactory.zoomTo(13));

                                map.moveCamera(CameraUpdateFactory.newLatLng(location));
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

        processData(region, map, visitor);

        LatLngBounds bounds = visitor.getLatLngBounds();

        return bounds;
    }

    /**
     * Populate map with markers.
     *
     * @param region
     * @return The view bounds.
     */
    private void populateMap(String region)
    {
        AddMarkerVisitor visitor = new AddMarkerVisitor(map);

        processData(region, map, visitor);
    }

    /**
     * Process data using defined visitor.
     *
     * @return The view bounds.
     */
    private void processData(String region, GoogleMap map, MapVisitor visitor)
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
                    String categoryClass = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY_CLASS));
                    String category = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY));
                    String severity = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_SEVERITY));
                    String road = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_ROAD));
                    String description = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_DESCRIPTION));

                    String latitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LATITUDE));
                    String longitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LONGITUDE));
                    String title = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_TITLE));

                    Double lat = Double.parseDouble(latitude);
                    Double lng = Double.parseDouble(longitude);

                    LatLng location = new LatLng(lat, lng);

                    // Visit the data
                    visitor.visit(new LatLng(lat, lng), title);

                    // Next
                    cursor.moveToNext();
                }
            }

        }
        catch (Throwable t)
        {
            Log.e(TrafficDataMapFragment.class.getCanonicalName(), t.getLocalizedMessage());
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