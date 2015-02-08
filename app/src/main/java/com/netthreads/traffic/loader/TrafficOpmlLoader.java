package com.netthreads.traffic.loader;

import android.content.Context;
import android.content.res.AssetManager;

import com.netthreads.rss.OpmlData;
import com.netthreads.rss.OpmlDataFactory;
import com.netthreads.rss.OpmlPullParser;
import com.netthreads.rss.StreamParser;
import com.netthreads.rss.StreamParserImpl;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rut19830 on 07/01/2015.
 */
public class TrafficOpmlLoader
{
    private static final String OPML_FILE_NAME = "feeds.opml";

    private Context context;

    /**
     * Create loader.
     *
     * @param context
     */
    public TrafficOpmlLoader(Context context)
    {
        this.context = context;
    }

    /**
     * Fetch local opml definitions.
     *
     * @return List of items.
     *
     * @throws FileNotFoundException
     * @throws XmlPullParserException
     */
    public List<OpmlData> loadOpml() throws IOException, XmlPullParserException
    {
        // -----------------------------------------------------------
        // Load exclusion list
        // -----------------------------------------------------------
        StreamParser<OpmlData> opmlStreamParser = new StreamParserImpl<OpmlData>();
        opmlStreamParser.addParser(new OpmlPullParser(opmlStreamParser.getParser()));

        List<OpmlData> opmlDataList = new ArrayList<OpmlData>();

        OpmlDataFactory opmlDataFactory = new OpmlDataFactory();

        AssetManager assetManager = context.getResources().getAssets();
        InputStream inputStream = assetManager.open(OPML_FILE_NAME);

        opmlStreamParser.fetch(inputStream, opmlDataList, opmlDataFactory);

        return opmlDataList;
    }

}
