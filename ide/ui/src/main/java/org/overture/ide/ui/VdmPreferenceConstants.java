package org.overture.ide.ui;

/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/


import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.NewScriptProjectPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.overture.ide.ui.partitioning.IVdmColorConstants;

public class VdmPreferenceConstants extends PreferenceConstants {

	
	/*
	 * Single line comment
	 */	
	/**
	 * A named preference that holds the color used to render single line
	 * comments.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_COLOR = IVdmColorConstants.VDM_COMMENT;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_BOLD = IVdmColorConstants.VDM_COMMENT
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_ITALIC = IVdmColorConstants.VDM_COMMENT
			+ EDITOR_ITALIC_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in strikethrough.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in strikethrough. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_STRIKETHROUGH = IVdmColorConstants.VDM_COMMENT
			+ EDITOR_STRIKETHROUGH_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in underline.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in underline. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 * 
	 * 
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_UNDERLINE = IVdmColorConstants.VDM_COMMENT
			+ EDITOR_UNDERLINE_SUFFIX;

	/*
	 * Overture doc
	 */
	/**
	 * A named preference that holds the color used to render overture doc
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String VDMSL_DOC_COLOR = IVdmColorConstants.VDM_DOC;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String VDM_DOC_COLOR_BOLD = IVdmColorConstants.VDM_DOC
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String VDM_DOC_COLOR_ITALIC = IVdmColorConstants.VDM_DOC
			+ EDITOR_ITALIC_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in strikethrough.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in strikethrough. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String VDM_DOC_COLOR_STRIKETHROUGH = IVdmColorConstants.VDM_DOC
			+ EDITOR_STRIKETHROUGH_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in underline.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in underline. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 * 
	 * 
	 */
	public final static String VDM_DOC_COLOR_UNDERLINE = IVdmColorConstants.VDM_DOC
			+ EDITOR_UNDERLINE_SUFFIX;

	/*
	 * Key worlds
	 */
	/**
	 * A named preference that holds the color used to render keyword.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_KEYWORD_COLOR = IVdmColorConstants.VDM_KEYWORD;

	/**
	 * A named preference that controls whether kwyword are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_BOLD = IVdmColorConstants.VDM_KEYWORD
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether keyword are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_ITALIC = IVdmColorConstants.VDM_KEYWORD
			+ EDITOR_ITALIC_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in strikethrough.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in strikethrough. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_STRIKETHROUGH = IVdmColorConstants.VDM_KEYWORD
			+ EDITOR_STRIKETHROUGH_SUFFIX;

	/**
	 * A named preference that controls whether keyword are rendered in
	 * underline.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in underline. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 * 
	 * 
	 */
	public final static String EDITOR_KEYWORD_UNDERLINE = IVdmColorConstants.VDM_KEYWORD
			+ EDITOR_UNDERLINE_SUFFIX;
	/*
	 * keyword return color
	 */
	/**
	 * A named preference that holds the color used to render keyword.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_KEYWORD_RETURN_COLOR = IVdmColorConstants.VDM_KEYWORD_RETURN;

	/**
	 * A named preference that controls whether kwyword are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_RETURN_BOLD = IVdmColorConstants.VDM_KEYWORD_RETURN
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether keyword are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_RETURN_ITALIC = IVdmColorConstants.VDM_KEYWORD_RETURN
			+ EDITOR_ITALIC_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in strikethrough.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in strikethrough. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_RETURN_STRIKETHROUGH = IVdmColorConstants.VDM_KEYWORD_RETURN
			+ EDITOR_STRIKETHROUGH_SUFFIX;

	/**
	 * A named preference that controls whether keyword are rendered in
	 * underline.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in underline. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 * 
	 * 
	 */
	public final static String EDITOR_KEYWORD_RETURN_UNDERLINE = IVdmColorConstants.VDM_KEYWORD_RETURN
			+ EDITOR_UNDERLINE_SUFFIX;

	/*
	 * Numbers
	 */
	/**
	 * A named preference that holds the color used to render NUMBER.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_NUMBER_COLOR = IVdmColorConstants.VDM_NUMBER;

	/**
	 * A named preference that controls whether number are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_NUMBER_BOLD = IVdmColorConstants.VDM_NUMBER + EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether NUMBER are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_NUMBER_ITALIC = IVdmColorConstants.VDM_NUMBER
			+ EDITOR_ITALIC_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in strikethrough.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in strikethrough. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_NUMBER_STRIKETHROUGH = IVdmColorConstants.VDM_NUMBER
			+ EDITOR_STRIKETHROUGH_SUFFIX;

	/**
	 * A named preference that controls whether NUMBER are rendered in
	 * underline.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in underline. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 * 
	 * 
	 */

	public final static String EDITOR_NUMBER_UNDERLINE = IVdmColorConstants.VDM_NUMBER
			+ EDITOR_UNDERLINE_SUFFIX;

	/*
	 * Strings
	 */
	/**
	 * A named preference that holds the color used to render STRING.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a
	 * string using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_STRING_COLOR = IVdmColorConstants.VDM_STRING;

	/**
	 * A named preference that controls whether STRING are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_STRING_BOLD = IVdmColorConstants.VDM_STRING
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether STRING are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_STRING_ITALIC = IVdmColorConstants.VDM_STRING
			+ EDITOR_ITALIC_SUFFIX;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in strikethrough.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in strikethrough. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_STRING_STRIKETHROUGH = IVdmColorConstants.VDM_STRING + EDITOR_STRIKETHROUGH_SUFFIX;

	/**
	 * A named preference that controls whether STRING are rendered in
	 * underline.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in underline. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_STRING_UNDERLINE = IVdmColorConstants.VDM_STRING + EDITOR_UNDERLINE_SUFFIX;

	public final static String EDITOR_FUNCTION_DEFINITION_COLOR = IVdmColorConstants.VDM_FUNCTION_DEFINITION;

//	public static final String EDITOR_XML_TAG_NAME_COLOR = IVdmColorConstants.VDM_XML_TAG_NAME;
//
//	public static final String EDITOR_XML_COMMENT_COLOR = IVdmColorConstants.VDM_XML_COMMENT_NAME;
//
//	public static final String EDITOR_XML_BODY_ALL = IVdmColorConstants.VDM_XML_ALL;
//
//	public static final String EDITOR_XML_ATTR_NAME_COLOR = IVdmColorConstants.VDM_XML_ATTR_NAME;
//
//	private static final String EDITOR_XML_TAG_NAME_BOLD = IVdmColorConstants.VDM_XML_TAG_NAME + EDITOR_BOLD_SUFFIX;
//
//	private static final String EDITOR_XML_ATTR_NAME_ITALIC = IVdmColorConstants.VDM_XML_ATTR_NAME + EDITOR_ITALIC_SUFFIX;

	public final static String COMMENT_TASK_TAGS = IVdmColorConstants.VDM_TODO_TAG;

	public final static String COMMENT_TASK_TAGS_BOLD = COMMENT_TASK_TAGS + EDITOR_BOLD_SUFFIX;
	
	public static final String VDM_OPERATOR = IVdmColorConstants.VDM_OPERATOR;
	public static final String VDM_TYPE = IVdmColorConstants.VDM_TYPE;
	public static final String VDM_CONSTANT = IVdmColorConstants.VDM_CONSTANT;
	public static final String VDM_FUNCTION = IVdmColorConstants.VDM_FUNCTION;
	public static final String VDM_PREDICATE = IVdmColorConstants.VDM_PREDICATE;
	
	
	
	
	public static void initializeDefaultValues(IPreferenceStore store) {
		PreferenceConstants.initializeDefaultValues(store);

		PreferenceConverter.setDefault(store, VDMSL_DOC_COLOR, new RGB(71, 102, 194));
		PreferenceConverter.setDefault(store, EDITOR_SINGLE_LINE_COMMENT_COLOR, new RGB(63, 127, 95));
		PreferenceConverter.setDefault(store, EDITOR_KEYWORD_COLOR, new RGB( 127, 0, 85));
		PreferenceConverter.setDefault(store, EDITOR_KEYWORD_RETURN_COLOR, new RGB(127, 0, 85));
		PreferenceConverter.setDefault(store, EDITOR_STRING_COLOR, new RGB(42, 0, 255));
		PreferenceConverter.setDefault(store, EDITOR_NUMBER_COLOR, new RGB(128, 0, 0));
		PreferenceConverter.setDefault(store, EDITOR_FUNCTION_DEFINITION_COLOR, new RGB(0, 0, 0));
//		PreferenceConverter.setDefault(store, EDITOR_XML_COMMENT_COLOR, new RGB(170, 200, 200));
//		PreferenceConverter.setDefault(store, EDITOR_XML_BODY_ALL, new RGB(240, 240, 240));
		PreferenceConverter.setDefault(store, VDM_OPERATOR, new RGB(255, 0, 0));
		PreferenceConverter.setDefault(store, VDM_TYPE, new RGB(0, 0, 255));
		PreferenceConverter.setDefault(store, VDM_CONSTANT, new RGB(0, 0, 200));
		PreferenceConverter.setDefault(store, VDM_FUNCTION, new RGB(0, 0, 255));
		PreferenceConverter.setDefault(store, VDM_PREDICATE, new RGB(0, 0, 255));
		
		
		store.setDefault(VDM_PREDICATE + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(VDM_PREDICATE + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		store.setDefault(VDM_FUNCTION + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(VDM_FUNCTION + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		store.setDefault(VDM_CONSTANT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(VDM_CONSTANT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

		store.setDefault(VDM_TYPE + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(VDM_TYPE + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);		
		
		store.setDefault(VDM_OPERATOR + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(VDM_OPERATOR + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		
		store.setDefault(EDITOR_SINGLE_LINE_COMMENT_BOLD, false);
		store.setDefault(EDITOR_SINGLE_LINE_COMMENT_ITALIC, false);

		store.setDefault(EDITOR_KEYWORD_BOLD, true);
		store.setDefault(EDITOR_KEYWORD_ITALIC, false);
//		store.setDefault(EDITOR_XML_TAG_NAME_BOLD, true);
//		store.setDefault(EDITOR_XML_ATTR_NAME_ITALIC, true);
		store.setDefault(EDITOR_KEYWORD_RETURN_BOLD, true);
		store.setDefault(EDITOR_KEYWORD_RETURN_ITALIC, false);

		store.setDefault(PreferenceConstants.EDITOR_SMART_INDENT, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_STRINGS, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACKETS, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACES, true);
		store.setDefault(PreferenceConstants.EDITOR_SMART_TAB, true);
		store.setDefault(PreferenceConstants.EDITOR_SMART_PASTE, true);
		store.setDefault(PreferenceConstants.EDITOR_SMART_HOME_END, true);
		store.setDefault(PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION, true);
		store.setDefault(PreferenceConstants.EDITOR_TAB_WIDTH, 8);
		store.setDefault(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);

		// folding
		// folding
		initializeFoldingDefaults(store);
		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.TAB);
		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
		store.setDefault(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "4");
		NewScriptProjectPreferencePage.initDefaults(store);
		store.setDefault(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES, false);
		store.setDefault(PreferenceConstants.APPEARANCE_METHOD_RETURNTYPE, false);
		store.setDefault(PreferenceConstants.APPEARANCE_METHOD_TYPEPARAMETERS, true);
		store.setDefault(PreferenceConstants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW, ""); //$NON-NLS-1$
		store.setDefault(PreferenceConstants.SHOW_SOURCE_MODULE_CHILDREN, true);
		store.setDefault(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS, ".");

		
//		store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
//		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);
//		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_DEFAULT_FOLDED, true);
//		// store.setDefault(PreferenceConstants.EDITOR_FOLDING_INNERTYPES,
//		// false);
//		// store.setDefault(PreferenceConstants.EDITOR_FOLDING_METHODS, false);
//		// store.setDefault(PreferenceConstants.EDITOR_FOLDING_IMPORTS, true);
//
//		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.TAB);
//		store.setDefault(CodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
//		store.setDefault(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "4");
//
//		NewScriptProjectPreferencePage.initDefaults(store);
//
//		store.setDefault(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES, false);
//		store.setDefault(PreferenceConstants.APPEARANCE_METHOD_RETURNTYPE, false);
//		store.setDefault(PreferenceConstants.APPEARANCE_METHOD_TYPEPARAMETERS, true);
//		store.setDefault(PreferenceConstants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW, ""); //$NON-NLS-1$
//
//		store.setDefault(PreferenceConstants.SHOW_SOURCE_MODULE_CHILDREN, true);
//
//		store.setDefault(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS, ".");
	}
	
	protected static void initializeFoldingDefaults(IPreferenceStore store) {
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT, 2);
		store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_DOCS_FOLDING_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_FOLDING_INIT_COMMENTS, true);
	}

}
