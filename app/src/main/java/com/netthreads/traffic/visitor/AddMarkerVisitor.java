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

package com.netthreads.traffic.visitor;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.netthreads.traffic.R;
import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.helper.ImageHelper;
import com.netthreads.traffic.view.TrafficDataHelper;

/**
 * Add marker to map.
 */
public class AddMarkerVisitor implements CursorVisitor
{
    private static final StringBuilder iconName = new StringBuilder(30);

    private GoogleMap map;

    public AddMarkerVisitor(GoogleMap map)
    {
        this.map = map;
    }

    /**
     * Draw markers.
     *
     * @param context This is here because we can't hold on to the context in local var.
     *
     * @param cursor
     */
    @Override
    public void visit(Context context, Cursor cursor)
    {
        String categoryClass = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY_CLASS));
        String title = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_TITLE));
        String severity = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_SEVERITY));
        String latitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LONGITUDE));

        Double lat = Double.parseDouble(latitude);
        Double lng = Double.parseDouble(longitude);

        iconName.setLength(0);
        iconName.append(categoryClass);
        iconName.append("_");
        iconName.append(severity);

        // NOTE we fetch icon based on category and severity.
        Bitmap bitmap = ImageHelper.fetchIcon(context, iconName.toString(), TrafficRecord.DEFAULT_ICON);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
    }
}
