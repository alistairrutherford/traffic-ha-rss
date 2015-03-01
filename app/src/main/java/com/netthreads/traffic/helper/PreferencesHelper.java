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

package com.netthreads.traffic.helper;

import android.content.SharedPreferences;

/**
 * Preference persistence lets settings save stuff in a more lightweight way rather than a full blown
 * content provider.
 */
public class PreferencesHelper
{
    private static final String REGION_KEY = "region_";

    /**
     * Initialise the region last loaded timestamps.
     *
     * @param sharedPreferences
     * @param regionNames
     */
    public static void initialiseRegionLastLoaded(SharedPreferences sharedPreferences, String[] regionNames)
    {
        long currentTime = System.currentTimeMillis();

        for (String title : regionNames)
        {
            String key = REGION_KEY + title;

            if (!sharedPreferences.contains(key))
            {
                sharedPreferences.edit().putLong(key, currentTime);
            }
        }

    }


    /**
     * Return region last loaded timestamp.
     *
     * @param sharedPreferences
     * @param region
     * @return The target last loaded timestamp.
     */
    public static long getRegionLastLoaded(SharedPreferences sharedPreferences, String region)
    {
        String key = REGION_KEY + region;

        long timestamp = sharedPreferences.getLong(key, 1000);

        return timestamp;
    }

    /**
     * Store region last loaded timestamp.
     *
     * @param sharedPreferences
     * @param region
     * @param timestamp
     */
    public static void setRegionLastLoaded(SharedPreferences sharedPreferences, String region, long timestamp)
    {
        String key = REGION_KEY + region;

        sharedPreferences.edit().putLong(key, timestamp);
    }

}
