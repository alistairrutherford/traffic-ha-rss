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
 *  https://developers.google.com/maps/documentation/android/start
 *
 */

package com.netthreads.traffic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.view.TrafficDataMapFragment;

public class MapActivity extends ActionBarActivity
{
    public static final String ARG_REGION = "dataRegion";
    public static final String ARG_ITEM = "item";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        if (savedInstanceState == null)
        {
            // Extract region
            String region = getIntent().getExtras().getString(ARG_REGION);

            // Extract bundle item.
            TrafficRecord data = (TrafficRecord) getIntent().getSerializableExtra(ARG_ITEM);

            TrafficDataMapFragment trafficDataMapFragment = TrafficDataMapFragment.newInstance(region, data);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, trafficDataMapFragment)
                    .commit();
        }
    }

    /**
     * Navigate with options.
     *
     *  Note: You need this or the parent activity onCreate will be called blowing away your
     *  settings.
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = getParentActivityIntent();
                if (upIntent != null)
                {
                    if (shouldUpRecreateTask(upIntent))
                    {
                        TaskStackBuilder.create(this)
                                // Add all of this activity's parents to the back stack
                                .addNextIntentWithParentStack(upIntent)
                                        // Navigate up to the closest parent
                                .startActivities();
                    }
                    else
                    {
                        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        navigateUpTo(upIntent);
                    }
                }
                return true;

            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
