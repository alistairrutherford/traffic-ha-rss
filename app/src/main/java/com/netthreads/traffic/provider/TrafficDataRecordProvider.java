package com.netthreads.traffic.provider;

import android.net.Uri;

import com.netthreads.traffic.domain.TrafficRecord;
import com.netthreads.easycp.annotation.ContentProvider;
import com.netthreads.easycp.contentprovider.EasyContentProvider;

/**
 * Content provider.
 * 
 */
@ContentProvider(database = TrafficDataRecordProvider.DATABASE_NAME, 
				authority = TrafficDataRecordProvider.AUTHORITY, 
				tableClass = TrafficRecord.class,
				version = TrafficDataRecordProvider.VERSION)
public class TrafficDataRecordProvider extends EasyContentProvider
{
	public static final String DATABASE_NAME = "traffic_data_records.db";

	public static final String AUTHORITY = "com.netthreads.traffic.data";

	public static final int VERSION = 2;
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + TrafficDataRecordProvider.AUTHORITY);
	
}
