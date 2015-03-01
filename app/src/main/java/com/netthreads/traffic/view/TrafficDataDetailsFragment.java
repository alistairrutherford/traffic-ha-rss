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


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netthreads.traffic.R;
import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.helper.ImageHelper;

/**
 * Data details fragment containing a simple view.
 */
public class TrafficDataDetailsFragment extends Fragment
{
    public static final String ARG_CATEGORY_CLASS = "categoryClass";
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_SEVERITY = "severity";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_DESCRIPTION = "description";

    private ImageView image;
    private TextView locationText;
    private TextView categoryText;
    private TextView descriptionText;

    /**
     * Create details view.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return Return view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_item_details, container, false);

        Context context = container.getContext();

        Bundle bundle = getArguments();

        String categoryClass = bundle.getString(ARG_CATEGORY_CLASS);
        String category = bundle.getString(ARG_CATEGORY);
        String severity = bundle.getString(ARG_SEVERITY);
        String location = bundle.getString(ARG_LOCATION);
        String description = bundle.getString(ARG_DESCRIPTION);

        // Set view elements from supplied data.
        String name = TrafficDataHelper.buildIconName(categoryClass, severity);

        Bitmap icon = ImageHelper.fetchIcon(context, name, TrafficRecord.DEFAULT_ICON);

        image = (ImageView) rootView.findViewById(R.id.event_image);
        locationText = (TextView) rootView.findViewById(R.id.event_location);
        descriptionText = (TextView) rootView.findViewById(R.id.event_description);
        categoryText = (TextView) rootView.findViewById(R.id.event_category);

        image.setImageBitmap(icon);
        locationText.setText(location);
        descriptionText.setText(description);
        categoryText.setText(category);

        return rootView;
    }
}