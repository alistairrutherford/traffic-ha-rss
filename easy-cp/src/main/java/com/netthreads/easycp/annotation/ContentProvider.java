/*
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
package com.netthreads.easycp.annotation;

import android.provider.BaseColumns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Content Provider annotation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
	ElementType.TYPE
})
public @interface ContentProvider
{
	public static final String DEFAULT_ID = BaseColumns._ID;
	public static final String DEFAULT_DB = "";
	public static final String DEFAULT_AUTHORITY = "";

	public String database() default DEFAULT_DB;

	public String authority() default DEFAULT_AUTHORITY;

	public Class<?> tableClass();

	public int version();

	public String idField() default DEFAULT_ID;
}
