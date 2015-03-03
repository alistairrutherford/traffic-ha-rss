/**
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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

/**
 * Settings Activity.
 * <p/>
 * Note: As long as the user makes any change to the settings we will signal
 * back to the calling activity that something has changed by passing RESULT_OK
 * in the return Intent. This is a bit naive as we should really do some sort of
 * before and after test.
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
    public static final int ACTIVITY_ID = 0x100;

    public static final String PREF_UPDATE_ON_START = "pref_update_on_start";
    public static final String PREF_AUTO_UPDATE     = "pref_auto_update";
    public static final String PREF_INCIDENT_SELECT = "pref_incident_select";

    public static final boolean DEFAULT_PREF_UPDATE_ON_START = true;
    public static final boolean DEFAULT_PREF_AUTO_UPDATE     = true;

    private int result = Activity.RESULT_CANCELED;

    /**
     * Define as static class.
     */
    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences_application);
        }
    }

    /**
     * Create activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    /**
     * On resume hook into shared preferences changed listener.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * On pause de-hook from shared preferences changed listener.
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Detect preference change so we can signal back to calling activity.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        result = Activity.RESULT_OK;

        // NOTE Technically we would check if something actually changed and set the result.

        this.setResult(result);
    }
}