/**
 * -----------------------------------------------------------------------
 * Copyright 2013 - Alistair Rutherford - www.netthreads.co.uk
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
 */
package com.netthreads.traffic.domain;

import com.netthreads.easycp.annotation.Column;
import com.netthreads.easycp.annotation.Table;

import java.io.Serializable;

/**
 * Traffic data data class which matches HA xml definition.
 */
@Table(name = TrafficRecord.TABLE_NAME)
public class TrafficRecord implements BaseColumns, Serializable
{
    public static final String DEFAULT_ICON = "incident_minor";

    public static final String TABLE_NAME = "traffic_data_record";

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    public static final String TEXT_TITLE = "title";
    public static final String TEXT_DESCRIPTION = "description";
    public static final String TEXT_ROAD = "road";
    public static final String TEXT_REGION = "region";
    public static final String TEXT_LATITUDE = "latitude";
    public static final String TEXT_LONGITUDE = "longitude";
    public static final String TEXT_EVENT_START = "eventStart";
    public static final String TEXT_EVENT_END = "eventEnd";
    public static final String TEXT_CATEGORY_CLASS = "categoryClass";
    public static final String TEXT_CATEGORY = "category";
    public static final String TEXT_SEVERITY = "severity";

    @Column(name = TEXT_TITLE)
    private String title;
    @Column(name = TEXT_DESCRIPTION)
    private String description;
    @Column(name = TEXT_CATEGORY_CLASS)
    private String categoryClass;
    @Column(name = TEXT_CATEGORY)
    private String category;
    @Column(name = TEXT_ROAD)
    private String road;
    @Column(name = TEXT_REGION)
    private String region;
    @Column(name = TEXT_LATITUDE)
    private String latitude;
    @Column(name = TEXT_LONGITUDE)
    private String longitude;
    @Column(name = TEXT_EVENT_START)
    private String eventStart;
    @Column(name = TEXT_EVENT_END)
    private String eventEnd;
    @Column(name = TEXT_SEVERITY)
    private String severity;

    public TrafficRecord()
    {
        // Empty constructor required.
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategoryClass()
    {
        return categoryClass;
    }

    public void setCategoryClass(String categoryClass)
    {
        this.categoryClass = categoryClass;
    }

    public String getRoad()
    {
        return road;
    }

    public void setRoad(String road)
    {
        this.road = road;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getEventStart()
    {
        return eventStart;
    }

    public void setEventStart(String eventStart)
    {
        this.eventStart = eventStart;
    }

    public String getEventEnd()
    {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd)
    {
        this.eventEnd = eventEnd;
    }

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(String severity)
    {
        this.severity = severity;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", " + categoryClass + ", " + road + ", " + region + ", " + latitude + ", " + longitude + ", " + eventStart + ", " + eventEnd + ", " + severity;
    }
}
