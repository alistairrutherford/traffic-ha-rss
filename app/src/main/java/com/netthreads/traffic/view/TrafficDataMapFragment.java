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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.netthreads.traffic.R;
import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.provider.TrafficDataRecordProvider;

/**
 * Map Fragment.
 */
public class TrafficDataMapFragment extends Fragment implements OnMapReadyCallback
{
    public static final String ARG_REGION = "region";
    public static final String ARG_LAT = "lat";
    public static final String ARG_LNG = "lng";

    private SupportMapFragment mapFragment;

    private String[] SELECT_REGIONS = {""};
    private String WHERE_REGION = TrafficRecord.TEXT_REGION + "= ?";

    private LatLng DEFAULT_BOUNDS_NE = new LatLng(55.811741, 1.768960);
    private LatLng DEFAULT_BOUNDS_SW = new LatLng(49.871159, -6.379880);

    /**
     * Construct fragment.
     *
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
     *
     * @return The view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        return rootView;
    }

    /**
     * On Map Ready.
     *
     * @param map
     */
    @Override
    public void onMapReady(GoogleMap map)
    {
        Projection projection = map.getProjection();

        Bundle bundle = getArguments();

        String region = bundle.getString(ARG_REGION);
        String lat = bundle.getString(ARG_LAT);
        String lng = bundle.getString(ARG_LNG);

        // load region and generate view bounds.
        LatLngBounds bounds = populateMarkers(map, region);

        // Set the camera to the greatest possible zoom level that includes the
        // bounds
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));

        // Centre if directed to.
        if (lat != null && lng != null)
        {
            Double latitude = Double.parseDouble(lat);
            Double longitude = Double.parseDouble(lng);

            LatLng location = new LatLng(latitude.doubleValue(), longitude.doubleValue());

            map.moveCamera(CameraUpdateFactory.zoomTo(13));

            map.moveCamera(CameraUpdateFactory.newLatLng(location));
        }

    }

    /**
     * Populate markers on map and return a bounds to set the view bounds.
     *
     * @param map
     * @param region
     * @return The view bounds.
     */
    private LatLngBounds populateMarkers(GoogleMap map, String region)
    {
        LatLngBounds.Builder builder = LatLngBounds.builder();

        // Default to England bounds in case there is no data.
        builder.include(DEFAULT_BOUNDS_NE);
        builder.include(DEFAULT_BOUNDS_SW);

        map.clear();

        Cursor cursor = null;

        try
        {
            SELECT_REGIONS[0] = region;
            cursor = getActivity().getContentResolver().query(TrafficDataRecordProvider.CONTENT_URI,
                    TrafficDataListFragment.PROJECTION,
                    WHERE_REGION,
                    SELECT_REGIONS,
                    null);

            while (cursor.isAfterLast() == false)
            {
                String categoryClass = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY_CLASS));
                String category = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY));
                String severity = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_SEVERITY));
                String road = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_ROAD));
                String title = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_DESCRIPTION));
                String latitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LONGITUDE));

                Double lat = Double.parseDouble(latitude);
                Double lng = Double.parseDouble(longitude);

                LatLng location = new LatLng(lat, lng);

                builder.include(new LatLng(lat, lng));

                // TODO color according to severity
                // TODO change marker for category class
                map.addMarker(new MarkerOptions()
                        .position(location)
                        .title(title));

                // Next
                cursor.moveToNext();
            }

        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }

        return builder.build();
    }
}