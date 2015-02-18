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
import com.google.android.gms.maps.model.MarkerOptions;
import com.netthreads.traffic.R;

/**
 * Map Fragment.
 */
public class TrafficDataMapFragment extends Fragment implements OnMapReadyCallback
{
    public static final String ARG_LAT  = "lat";
    public static final String ARG_LNG  = "lng";
    public static final String ARG_INFO = "info";

    private SupportMapFragment mapFragment;

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

        return rootView;
    }

    /**
     * On Map Ready.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Projection projection = googleMap.getProjection();

        Bundle bundle = getArguments();

        String lat = bundle.getString(ARG_LAT);
        String lng = bundle.getString(ARG_LNG);
        String info = bundle.getString(ARG_INFO);

        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);

        LatLng location = new LatLng(latitude.doubleValue(), longitude.doubleValue());

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(info));

    }
}