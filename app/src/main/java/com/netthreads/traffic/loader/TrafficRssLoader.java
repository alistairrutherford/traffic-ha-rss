package com.netthreads.traffic.loader;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.netthreads.rss.RssPullParser;
import com.netthreads.rss.StreamParser;
import com.netthreads.rss.StreamParserImpl;
import com.netthreads.rss.data.traffic.TrafficData;
import com.netthreads.rss.data.traffic.TrafficDataFactory;
import com.netthreads.rss.data.traffic.TrafficPullParser;
import com.netthreads.traffic.SettingsActivity;
import com.netthreads.traffic.domain.TrafficRecord;

import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Data Loader.
 * <p/>
 * Load manager handles control of this.
 */
public class TrafficRssLoader extends AsyncTaskLoader<Object>
{
    private static final String LOG_TAG = "TrafficRssLoader";

    public static final int LOADER_ID = 0x02;

    private static final StringBuilder urlStringBuilder = new StringBuilder(255);

    private Uri uri;
    private String url;
    private String region;

    private String[] SELECT_REGIONS = {""};
    private String WHERE_REGION = TrafficRecord.TEXT_REGION + "= ?";

    private ContentValues contentValues;
    private int state;
    private StreamParser<TrafficData> parser;

    /**
     * Construct Loader.
     * <p/>
     * note we don't use 'context' directly but instead the save global
     * application context returned by getContext().
     *
     * @param context
     * @param uri
     * @throws XmlPullParserException
     */
    public TrafficRssLoader(Context context, Uri uri) throws XmlPullParserException
    {
        super(context);

        this.uri = uri;

        this.url = null;

        this.region = null;

        contentValues = new ContentValues();

        parser = new StreamParserImpl<TrafficData>();
        parser.addParser(new RssPullParser());
        parser.addParser(new TrafficPullParser());
    }

    /**
     * This is where the bulk of our work is done. This function is called in a
     * background thread and should generate a new set of data to be published
     * by the loader.
     */
    @Override
    public Void loadInBackground()
    {
        if (url != null && !url.isEmpty())
        {
            try
            {
                // Load data
                URL urlEntity = new URL(url);

                InputStream inputStream = urlEntity.openStream();

                List<TrafficData> list = new LinkedList<TrafficData>();
                TrafficDataFactory trafficDataFactory = new TrafficDataFactory();

                // -----------------------------------------------------------
                // Load filter preferences.
                // -----------------------------------------------------------
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                Set<String> filter = sharedPreferences.getStringSet(SettingsActivity.PREF_INCIDENT_SELECT, null);

                // Load data.
                parser.fetch(inputStream, list, trafficDataFactory);

                // If not cancelled then write results to database.
                try
                {
                    if (!list.isEmpty())
                    {
                        // ---------------------------------------------------------------
                        // Delete all current items for target region.
                        // ---------------------------------------------------------------
                        SELECT_REGIONS[0] = region;
                        getContext().getContentResolver().delete(uri, WHERE_REGION, SELECT_REGIONS);

                        // ---------------------------------------------------------------
                        // Load new ones (if not filtered out).
                        // ---------------------------------------------------------------
                        for (TrafficData trafficData : list)
                        {
                            String categoryClass = trafficData.getCategoryClass();

                            // If one of our valid filter categories
                            if (validCategory(filter, categoryClass))
                            {
                                Log.i(this.getClass().getCanonicalName(), trafficData.toString());

                                // ---------------------------------------------------------------
                                // Load new ones.
                                // ---------------------------------------------------------------
                                contentValues.clear();

                                contentValues.put(TrafficRecord.TEXT_CATEGORY_CLASS, categoryClass);
                                contentValues.put(TrafficRecord.TEXT_CATEGORY, trafficData.getCategory());
                                contentValues.put(TrafficRecord.TEXT_SEVERITY, trafficData.getSeverity());
                                contentValues.put(TrafficRecord.TEXT_ROAD, trafficData.getRoad());
                                contentValues.put(TrafficRecord.TEXT_REGION, region);
                                contentValues.put(TrafficRecord.TEXT_TITLE, trafficData.getTitle());
                                contentValues.put(TrafficRecord.TEXT_DESCRIPTION, trafficData.getDescription());
                                contentValues.put(TrafficRecord.TEXT_LATITUDE, trafficData.getLatitude());
                                contentValues.put(TrafficRecord.TEXT_LONGITUDE, trafficData.getLongitude());
                                contentValues.put(TrafficRecord.TEXT_EVENT_START, trafficData.getEventStart());
                                contentValues.put(TrafficRecord.TEXT_EVENT_END, trafficData.getEventEnd());

                                // Insert into content provider.
                                getContext().getContentResolver().insert(uri, contentValues);
                            }

                        }
                    }
                }
                finally
                {
                    inputStream.close();
                }

            }
            catch (Throwable t)
            {
                Log.e(LOG_TAG, t.getLocalizedMessage());
            }
        }

        // Done!
        return null;
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading()
    {
        super.onStopLoading();

        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handle cancelled.
     *
     * @return
     */
    @Override
    public boolean cancelLoad()
    {
        parser.cancel();

        return true;
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(Object aVoid)
    {
        super.onCanceled(aVoid);

        parser.reset();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset()
    {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();
    }

    /**
     * Check if preferences filter indicates we should load this class of event.
     *
     * @param filter
     * @param categoryClass
     * @return True if valid.
     */
    private boolean validCategory(Set<String> filter, String categoryClass)
    {
        boolean found = filter.contains(categoryClass);

        return found;
    }

    /**
     * Return loader parser state.
     *
     * @return loader parser state.
     */
    public int getState()
    {
        return state;
    }

    /**
     * Set URL of data to load.
     *
     * @param url
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Set region to filter on.
     *
     * @param region
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
}