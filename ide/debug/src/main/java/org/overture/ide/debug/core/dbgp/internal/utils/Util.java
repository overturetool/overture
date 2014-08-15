/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.dbgp.internal.utils;

public class Util
{

	/**
	 * Some input streams return available() as zero, so we need this value.
	 */
	// private static final int DEFAULT_READING_SIZE = 8192;
	public final static String UTF_8 = "UTF-8"; //$NON-NLS-1$			
	public static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	/**
	 * @since 2.0
	 */
	public static final Object[] EMPTY_ARRAY = new Object[0];

}
