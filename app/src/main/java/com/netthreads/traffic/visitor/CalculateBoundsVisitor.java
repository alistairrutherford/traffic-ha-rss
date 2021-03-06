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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.netthreads.traffic.domain.TrafficRecord;

/**
 * Calculate bounds.
 *
 */
public class CalculateBoundsVisitor implements CursorVisitor
{
    private LatLngBounds.Builder builder = LatLngBounds.builder();

    public CalculateBoundsVisitor()
    {
        builder = LatLngBounds.builder();
    }

    @Override
    public void visit(Context context, Cursor cursor)
    {
        String latitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LATITUDE));
        String longitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LONGITUDE));
        String title = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_TITLE));

        Double lat = Double.parseDouble(latitude);
        Double lng = Double.parseDouble(longitude);

        builder.include(new LatLng(lat, lng));
    }

    public LatLngBounds getLatLngBounds()
    {
        return builder.build();
    }
}
