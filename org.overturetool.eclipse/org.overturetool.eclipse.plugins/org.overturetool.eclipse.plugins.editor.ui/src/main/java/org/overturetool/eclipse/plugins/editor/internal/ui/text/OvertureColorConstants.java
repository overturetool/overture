/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import org.eclipse.dltk.ui.text.DLTKColorConstants;

public final class OvertureColorConstants {
	private OvertureColorConstants() {
	}

	/**
	 * The color key for string and character literals in Overture code.
	 */
	public static final String OVERTURE_STRING = DLTKColorConstants.DLTK_STRING; //$NON-NLS-1$

	/**
	 * The color key for Overture comments.
	 */
	public static final String OVERTURE_SINGLE_LINE_COMMENT = DLTKColorConstants.DLTK_SINGLE_LINE_COMMENT; //$NON-NLS-1$	

	/**
	 * The color key for Overture doc.
	 */
	public static final String OVERTURE_DOC = "OVERTURE_DOC"; //$NON-NLS-1$	

	/**
	 * The color key for Overture numbers.
	 */
	public static final String OVERTURE_NUMBER = DLTKColorConstants.DLTK_NUMBER; //$NON-NLS-1$

	/**
	 * The color key for Overture keywords.
	 */
	public static final String OVERTURE_KEYWORD = DLTKColorConstants.DLTK_KEYWORD; //$NON-NLS-1$

	/**
	 * The color key for Overture keyword 'return'.
	 */
	public static final String OVERTURE_KEYWORD_RETURN = DLTKColorConstants.DLTK_KEYWORD_RETURN; //$NON-NLS-1$	

	/**
	 * The color key for Overture code.
	 */
	public static final String OVERTURE_DEFAULT = DLTKColorConstants.DLTK_DEFAULT; //$NON-NLS-1$

	/**
	 * The color key for TO-DO tasks in comments.
	 */
	public static final String OVERTURE_TODO_TAG = DLTKColorConstants.TASK_TAG;

	/**
	 * The color key for Overture function definition.
	 */
	public static final String OVERTURE_FUNCTION_DEFINITION = DLTKColorConstants.DLTK_FUNCTION_DEFINITION;

//	public static final String OVERTURE_XML_TAG_NAME = "XML_TAG_NAME";
//
//	public static final String OVERTURE_XML_ATTR_NAME = "XML_ATTR_NAME";
//
//	public static final String OVERTURE_XML_COMMENT_NAME = "XML_COMMENT_NAME";
//
//	public static final String OVERTURE_XML_ALL = "XML_ALL";
	
	public static final String OVERTURE_OPERATOR = "overture_operators";
	public static final String OVERTURE_TYPE = "overture_types";
	public static final String OVERTURE_CONSTANT = "overture_constants";
	public static final String OVERTURE_FUNCTION = "overture_functions";
	public static final String OVERTURE_PREDICATE = "overture_predicates";
	

}
