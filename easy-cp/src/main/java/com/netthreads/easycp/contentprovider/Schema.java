/*
 * -----------------------------------------------------------------------
 * Copyright (C) 2013 Alistair Rutherford
 * -----------------------------------------------------------------------
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netthreads.easycp.contentprovider;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netthreads.easycp.annotation.Column;
import com.netthreads.easycp.annotation.ContentProvider;
import com.netthreads.easycp.annotation.Table;
import com.netthreads.easycp.contentprovider.SQLLiteHelper.SQLiteType;
import com.netthreads.easycp.exception.EasyContentProviderException;

/**
 * Schema Definition.
 * 
 */
public class Schema
{
	private static final String ERROR_NO_CONTENT_PROVIDER_ANNOTATION_PRESENT = "No @ContentProvider annotation present.";
	private static final String ERROR_NO_RECOGNISED_FOR_COLUMN = "No type found for annotated column.";

	private final String databaseName;
	private final String authorityName;
	private final String tableName;
	private final Map<String, SQLLiteHelper.SQLiteType> columnDefinitions;
	private final int version;
	private final String idField;

	/**
	 * Construct table definition.
	 * 
	 * @param easyCP
	 *            Instance of EasyContentProvider.
	 */
	public Schema(Class<? extends EasyContentProvider> easyCP)
	{
		final ContentProvider contentProviderAnnotation = easyCP.getAnnotation(ContentProvider.class);

		if (contentProviderAnnotation != null)
		{
			// -----------------------------------------------------------
			// Process ContentProvider
			// -----------------------------------------------------------
			databaseName = contentProviderAnnotation.database();

			authorityName = contentProviderAnnotation.authority();

			version = contentProviderAnnotation.version();

			idField = contentProviderAnnotation.idField();

			Class<?> tableClass = contentProviderAnnotation.tableClass();

			// -----------------------------------------------------------
			// Process table
			// -----------------------------------------------------------
			final Table tableAnnotation = tableClass.getAnnotation(Table.class);

			tableName = tableAnnotation.name();

			List<Field> fields = new ArrayList<Field>(Arrays.asList(tableClass.getDeclaredFields()));

			// -----------------------------------------------------------
			// Process columns
			// -----------------------------------------------------------
			columnDefinitions = new HashMap<String, SQLLiteHelper.SQLiteType>();
			
			for (Field field : fields)
			{
				final Column columnAnnotation = field.getAnnotation(Column.class);

				if (field.isAnnotationPresent(Column.class))
				{
					Class<?> type = field.getType();

					SQLiteType sqlLiteType = SQLLiteHelper.TYPE_MAP.get(type);

					if (sqlLiteType != null)
					{
						columnDefinitions.put(columnAnnotation.name(), sqlLiteType);
					}
					else
					{
						throw new EasyContentProviderException(ERROR_NO_RECOGNISED_FOR_COLUMN);
					}
				}
			}
		}
		else
		{
			throw new EasyContentProviderException(ERROR_NO_CONTENT_PROVIDER_ANNOTATION_PRESENT);
		}
	}

	/**
	 * Return database name.
	 * 
	 * @return The database name.
	 */
	public String getDatabaseName()
	{
		return databaseName;
	}

	/**
	 * Return authority name.
	 * 
	 * @return The authority name.
	 */
	public String getAuthorityName()
	{
		return authorityName;
	}

	/**
	 * Return table name.
	 * 
	 * @return The table name.
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * Return column definitions.
	 * 
	 * @return The column definitions.
	 */
	public Map<String, SQLLiteHelper.SQLiteType> getColumnDefinitions()
	{
		return columnDefinitions;
	}
	
	/**
	 * Return database version.
	 * 
	 * @return The database version.
	 */
	public int getDatabaseVersion()
	{
		return version;
	}

	/**
	 * Return ID field name.
	 * 
	 * @return The ID field name.
	 */
	public String getIdField()
	{
		return idField;
	}

}
