/**
 *
 * -----------------------------------------------------------------------
 * Copyright 2015 - Alistair Rutherford - www.netthreads.co.uk
 * -----------------------------------------------------------------------
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/

package com.netthreads.traffic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.traffic.view.TrafficDataDetailsFragment;

/**
 * Details Activity.
 *
 * Note: Extends support class ActionBarActivity.
 *
 */
public class DetailsActivity extends ActionBarActivity
{
    public static final String ARG_ITEM = "item";

    /**
     * Create activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        // NOTE: Support
        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Extract bundle item.
        TrafficRecord data = (TrafficRecord) getIntent().getSerializableExtra(ARG_ITEM);

        if (savedInstanceState == null)
        {
            TrafficDataDetailsFragment fragment =  new TrafficDataDetailsFragment();
            Bundle bundle = new Bundle();

            // Can't pass object so we'll have to pass individual values.
            bundle.putString(TrafficDataDetailsFragment.ARG_CATEGORY_CLASS, data.getCategoryClass());
            bundle.putString(TrafficDataDetailsFragment.ARG_CATEGORY, data.getCategory());
            bundle.putString(TrafficDataDetailsFragment.ARG_SEVERITY, data.getSeverity());
            bundle.putString(TrafficDataDetailsFragment.ARG_LOCATION, data.getRoad());
            bundle.putString(TrafficDataDetailsFragment.ARG_DESCRIPTION, data.getDescription());

            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    /**
     * Navigate with options.
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
