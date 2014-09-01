/*
 * #%~
 * Test Framework for Overture
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.test.framework;

public class Properties
{
	/**
	 * Flag used globally to re-created all test result files used for result comparison
	 */
	public static boolean recordTestResults = false;

	/**
	 * Flag used when writing result files to determine if UNIX-style line separators should be used. Default to true so
	 * that even Windows-based machines will generate LF rather than CRLF line endings.
	 */
	public static boolean forceUnixLineEndings = true;
}
