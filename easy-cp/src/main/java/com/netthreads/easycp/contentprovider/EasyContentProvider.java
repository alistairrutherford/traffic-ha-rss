/*
 * -----------------------------------------------------------------------
 * Copyright 2013 - Alistair Rutherford - www.netthreads.co.uk
 * -----------------------------------------------------------------------
 * Copyright (C) 2010 Michael Pardo
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
package com.netthreads.easycp.contentprovider;

import java.util.Collection;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.SparseArray;

import com.netthreads.easycp.contentprovider.SQLLiteHelper.SQLiteType;

/**
 * Easy Content Provider.
 *
 * Note: Some portions of this 'borrowed' from ActiveAndroid
 * https://github.com/pardom/ActiveAndroid
 * 
 */
public abstract class EasyContentProvider extends ContentProvider
{
	public static final String DEFAULT_SORT_ORDER = BaseColumns._ID + " ASC";

	private Schema schema;
	private DatabaseHelper databaseHelper;

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private static SparseArray<String> MIME_TYPES = new SparseArray<String>();

	/**
	 * Create content provider based on annotation settings.
	 * 
	 */
	@Override
	public boolean onCreate()
	{
		schema = new Schema(this.getClass());

		databaseHelper = new DatabaseHelper(getContext(), schema);

		buildUriMatcher(schema);

		return true;
	}

	/**
	 * Issue query.
	 * 
	 * @param uri
	 *            The URI to query. This will be the full URI sent by the client; if the client is requesting a specific
	 *            record, the URI will end in a record number that the implementation should parse and add to a WHERE or
	 *            HAVING clause, specifying that _id value.
	 * @param projection
	 *            The list of columns to put into the cursor. If null all columns are included.
	 * @param selection
	 *            A selection criteria to apply when filtering rows. If null then all rows are included.
	 * @param sortOrder
	 *            How the rows in the cursor should be sorted. If null then the provider is free to define the sort
	 *            order.
	 * @return a Cursor or null.
	 * 
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		String tableName = schema.getTableName();

		// If no sort order is specified use the default
		String orderBy;

		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = DEFAULT_SORT_ORDER;
		}
		else
		{
			orderBy = sortOrder;
		}

		final Cursor cursor = database.query(tableName, projection, selection, selectionArgs, null, null, orderBy);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	/**
	 * Insert Row.
	 * 
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		final Long id = database.insert(schema.getTableName(), null, values);

		if (id != null && id > 0)
		{
			Uri retUri = createUri(id);

			getContext().getContentResolver().notifyChange(retUri, null);

			return retUri;
		}

		return null;
	}

	/**
	 * Update row.
	 * 
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		final int count = database.update(schema.getTableName(), values, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	/**
	 * Delete Row.
	 * 
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		int count = database.delete(schema.getTableName(), selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	/**
	 * Return the MIME type of the data at the given URI.
	 * 
	 * @param uri
	 */
	@Override
	public String getType(Uri uri)
	{
		final int match = URI_MATCHER.match(uri);

		String cachedMimeType = MIME_TYPES.get(match);

		if (cachedMimeType != null)
		{
			return cachedMimeType;
		}

		final boolean single = ((match % 2) == 0);

		StringBuilder mimeType = new StringBuilder();
		mimeType.append("vnd");
		mimeType.append(".");
		mimeType.append(schema.getAuthorityName());
		mimeType.append(".");
		mimeType.append(single ? "item" : "dir");
		mimeType.append("/");
		mimeType.append("vnd");
		mimeType.append(".");
		mimeType.append(schema.getAuthorityName());
		mimeType.append(".");
		mimeType.append(schema.getTableName());

		MIME_TYPES.append(match, mimeType.toString());

		return mimeType.toString();
	}

	/**
	 * Create URI
	 * 
	 * @param id
	 * 
	 * @return Target URI
	 */
	public Uri createUri(Long id)
	{
		final StringBuilder uri = new StringBuilder();
		uri.append("content://");
		uri.append(schema.getAuthorityName());
		uri.append("/");
		uri.append(schema.getTableName().toLowerCase());

		if (id != null)
		{
			uri.append("/");
			uri.append(id.toString());
		}

		return Uri.parse(uri.toString());
	}

	/**
	 * Build URI_MATCHER.
	 * 
	 * @param schema
	 */
	private void buildUriMatcher(Schema schema)
	{
		// ---------------------------------------------------------------
		// Build URI matcher.
		// ---------------------------------------------------------------
		Map<String, SQLiteType> columns = schema.getColumnDefinitions();
		Collection<String> columnNames = columns.keySet();
		int i = 0;
		for (String columnName : columnNames)
		{
			// AA
			final int tableKey = (i * 2) + 1;
			final int itemKey = (i * 2) + 2;

			URI_MATCHER.addURI(schema.getAuthorityName() + "/" + schema.getTableName(), columnName.toLowerCase(), tableKey);

			URI_MATCHER.addURI(schema.getAuthorityName() + "/" + schema.getTableName(), columnName.toLowerCase() + "/#", itemKey);
		}
	}

}
