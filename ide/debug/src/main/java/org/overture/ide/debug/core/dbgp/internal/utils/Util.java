/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.utils;


public class Util {

	/**
	 * Some input streams return available() as zero, so we need this value.
	 */
	private static final int DEFAULT_READING_SIZE = 8192;
	public final static String UTF_8 = "UTF-8"; //$NON-NLS-1$			
	public static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	/**
	 * @since 2.0
	 */
	public static final Object[] EMPTY_ARRAY = new Object[0];

}
