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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netthreads.traffic.R;
import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.helper.ImageHelper;

import java.util.HashMap;

/**
 * Data view holder.
 */
public class TrafficDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    private ImageView image;
    private TextView locationText;
    private TextView titleText;
    private TextView categoryText;
    private RelativeLayout relativeLayout;

    private View itemView;

    private IItemClickListener clickListener;

    // Image cache
    private static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();

    // Use a static string builder to avoid instantiating strings for every row
    private static final StringBuilder iconName = new StringBuilder(30);

    private TrafficRecord trafficRecord;

    /**
     * Construct view holder.
     *
     * @param itemView      The view.
     * @param clickListener The click listener
     */
    public TrafficDataViewHolder(View itemView, IItemClickListener clickListener)
    {
        super(itemView);

        this.itemView = itemView;
        this.clickListener = clickListener;

        // Create image cache.
        images = new HashMap<String, Bitmap>();

        image = (ImageView) itemView.findViewById(R.id.event_image);
        locationText = (TextView) itemView.findViewById(R.id.event_location);
        titleText = (TextView) itemView.findViewById(R.id.event_title);
        categoryText = (TextView) itemView.findViewById(R.id.event_category);
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.event_row);

        // Click handlers.
        relativeLayout.setOnClickListener(this);
        relativeLayout.setOnLongClickListener(this);

        trafficRecord = new TrafficRecord();
    }

    /**
     * Bind data to view.
     * <p/>
     * Note: We have categories and these are 'boiled down' to a class which gives us an appropriate icon.
     *
     * @param categoryClass
     * @param severity
     * @param road
     * @param region
     * @param title
     * @param latitude
     * @param longitude
     */
    public void bindTrafficData(
            String categoryClass,
            String category,
            String severity,
            String road,
            String region,
            String title,
            String description,
            String latitude,
            String longitude)
    {
        // Icon is one of our classes like accident, congestion, incident, roadwork
        Bitmap icon = getBitMap(categoryClass, severity);

        if (icon != null)
        {
            image.setImageBitmap(icon);
        }

        // Category is real category text like Major Incident, Sporting Event, Accident etc
        categoryText.setText(category);

        // Set location
        locationText.setText(road);

        // Set title
        titleText.setText(title);


        trafficRecord.setCategoryClass(categoryClass);
        trafficRecord.setCategory(category);
        trafficRecord.setSeverity(severity);
        trafficRecord.setTitle(title);
        trafficRecord.setDescription(description);
        trafficRecord.setRoad(road);
        trafficRecord.setRegion(region);
        trafficRecord.setLatitude(latitude);
        trafficRecord.setLongitude(longitude);
    }

    /**
     * Fetch appropriate image for category and severity.
     *
     * @param categoryClass
     * @param severity
     * @return Target image.
     */
    Bitmap getBitMap(String categoryClass, String severity)
    {
        iconName.setLength(0);
        iconName.append(categoryClass);
        iconName.append("_");
        iconName.append(severity);

        Bitmap icon = get(itemView.getContext(), iconName.toString(), TrafficRecord.DEFAULT_ICON);

        return icon;
    }

    /**
     * Get image from cache.
     *
     * @param name
     * @return Bitmap image
     */
    public Bitmap get(Context context, String name, String defaultName)
    {
        // Look in cache
        Bitmap target = images.get(name);

        if (target == null)
        {
            // If not found then fetch resource.
            target = ImageHelper.fetchIcon(context, name, defaultName);

            images.put(name, target);
        }

        return target;
    }

    /**
     * Click handler.
     *
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        clickListener.onClick(trafficRecord);
    }


    /**
     * On Long Click.
     *
     * @param v
     * @return True if handled.
     */
    @Override
    public boolean onLongClick(View v)
    {
        clickListener.onLongClick(trafficRecord);

        return true;
    }

    /**
     * Return item view.
     *
     * @return The item view.
     */
    public View getItemView()
    {
        return itemView;
    }
}
