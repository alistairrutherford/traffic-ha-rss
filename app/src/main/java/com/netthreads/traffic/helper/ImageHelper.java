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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.netthreads.traffic.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Resource image helper.
 *
 */
public class ImageHelper
{
    /**
     * Fetch icon from drawables cache.
     *
     * @param name
     * @return Icon bitmap
     * @throws java.io.IOException
     */
    public static Bitmap fetchIcon(Context context, String name, String defaultName)
    {
        Bitmap target = null;

        target = getImage(context, name);

        // If can't find what we want then fetch default.
        if (target == null)
        {
            target = getImage(context, defaultName);
        }

        return target;
    }

    /**
     * Get image from drawable list.
     *
     * @param context
     * @param name
     * @return Target Bitmap image or null if not found.
     */
    public static Bitmap getImage(Context context, String name)
    {
        Bitmap target = null;

        try
        {
            int value = getFieldValue(name, R.drawable.class);

            InputStream is = context.getResources().openRawResource(value);

            target = BitmapFactory.decodeStream(is);
        }
        catch (Throwable e)
        {
            // Not found
        }

        return target;
    }

    /**
     * Extract field integer value.
     *
     * @param name The field name.
     * @param obj  The target object.
     * @return The value
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("rawtypes")
    public static int getFieldValue(String name, Class obj) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field field = obj.getDeclaredField(name);

        int value = field.getInt(obj);

        return value;
    }

}
