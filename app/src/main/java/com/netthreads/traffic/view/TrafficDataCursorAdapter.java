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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netthreads.traffic.R;
import com.netthreads.traffic.domain.TrafficRecord;

/**
 * Test Data object adapter.
 */
public class TrafficDataCursorAdapter extends RecyclerView.Adapter<TrafficDataViewHolder>
{
    private IItemClickListener itemClickListener;

    private Cursor cursor;

    /**
     * Construct view holder.
     */
    public TrafficDataCursorAdapter(IItemClickListener itemClickListener)
    {
        this.cursor = null;

        this.itemClickListener = itemClickListener;
    }

    /**
     * Create view
     *
     * @param viewGroup
     * @param viewType
     * @return The view.
     */
    @Override
    public TrafficDataViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_item_list_row, viewGroup, false);

        TrafficDataViewHolder testDataViewHolder = new TrafficDataViewHolder(view, itemClickListener);

        return testDataViewHolder;
    }

    /**
     * Bind data to view.
     *
     * @param testDataViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(TrafficDataViewHolder testDataViewHolder, int position)
    {
        if (cursor != null)
        {
            cursor.moveToPosition(position);

            String categoryClass = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY_CLASS));
            String category = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_CATEGORY));
            String severity = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_SEVERITY));
            String road = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_ROAD));
            String region = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_REGION));
            String title = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_DESCRIPTION));
            String latitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndex(TrafficRecord.TEXT_LONGITUDE));

            testDataViewHolder.bindTrafficData(categoryClass, category, severity, road, region, title, description, latitude, longitude);
        }

    }


    /**
     * Change cursor.
     *
     * @param cursor
     */
    public void changeCursor(Cursor cursor)
    {
        Cursor old = swapCursor(cursor);
        if (old != null)
        {
            old.close();
        }
    }

    /**
     * Swap cursor.
     *
     * @param cursor
     * @return The new cursor.
     */
    public Cursor swapCursor(Cursor cursor)
    {
        if (this.cursor == cursor)
        {
            return null;
        }

        Cursor oldCursor = cursor;

        this.cursor = cursor;

        if (cursor != null)
        {
            this.notifyDataSetChanged();
        }

        return oldCursor;
    }

    /**
     * Get item an position.
     *
     * @param position
     * @return The target item.
     */
    private Object getItem(int position)
    {
        cursor.moveToPosition(position);

        // Load data from cursor and return it...

        return null;
    }

    /**
     * Return item count.
     *
     * @return The count.
     */
    @Override
    public int getItemCount()
    {
        return (cursor == null) ? 0 : cursor.getCount();
    }
}

