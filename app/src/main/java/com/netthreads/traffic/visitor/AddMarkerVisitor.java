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

import android.database.Cursor;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.netthreads.traffic.domain.TrafficRecord;

/**
 * Add marker to map.
 */
public class AddMarkerVisitor implements CursorVisitor
{
    private GoogleMap map;

    public AddMarkerVisitor(GoogleMap map)
    {
        this.map = map;
    }

    @Override
    public void visit(Cursor cursor)
    {
        String categoryClass = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY_CLASS));
        String title = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_TITLE));
        String severity = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_SEVERITY));
        String latitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LONGITUDE));

        Double lat = Double.parseDouble(latitude);
        Double lng = Double.parseDouble(longitude);

        // TODO icon class and colour.
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title));
    }
}
