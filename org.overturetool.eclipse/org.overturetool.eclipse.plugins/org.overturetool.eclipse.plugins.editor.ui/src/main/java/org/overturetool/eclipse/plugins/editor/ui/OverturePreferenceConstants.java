/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.ui;

import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.preferences.NewScriptProjectPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.OvertureColorConstants;

public class OverturePreferenceConstants extends PreferenceConstants {

	
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
	public final static String EDITOR_SINGLE_LINE_COMMENT_COLOR = OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_BOLD = OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT
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
	public final static String EDITOR_SINGLE_LINE_COMMENT_ITALIC = OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT
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
	public final static String EDITOR_SINGLE_LINE_COMMENT_STRIKETHROUGH = OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT
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
	public final static String EDITOR_SINGLE_LINE_COMMENT_UNDERLINE = OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT
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
	public final static String OVERTURE_DOC_COLOR = OvertureColorConstants.OVERTURE_DOC;

	/**
	 * A named preference that controls whether single line comments are
	 * rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String OVERTURE_DOC_COLOR_BOLD = OvertureColorConstants.OVERTURE_DOC
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
	public final static String OVERTURE_DOC_COLOR_ITALIC = OvertureColorConstants.OVERTURE_DOC
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
	public final static String OVERTURE_DOC_COLOR_STRIKETHROUGH = OvertureColorConstants.OVERTURE_DOC
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
	public final static String OVERTURE_DOC_COLOR_UNDERLINE = OvertureColorConstants.OVERTURE_DOC
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
	public final static String EDITOR_KEYWORD_COLOR = OvertureColorConstants.OVERTURE_KEYWORD;

	/**
	 * A named preference that controls whether kwyword are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_BOLD = OvertureColorConstants.OVERTURE_KEYWORD
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether keyword are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_ITALIC = OvertureColorConstants.OVERTURE_KEYWORD
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
	public final static String EDITOR_KEYWORD_STRIKETHROUGH = OvertureColorConstants.OVERTURE_KEYWORD
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
	public final static String EDITOR_KEYWORD_UNDERLINE = OvertureColorConstants.OVERTURE_KEYWORD
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
	public final static String EDITOR_KEYWORD_RETURN_COLOR = OvertureColorConstants.OVERTURE_KEYWORD_RETURN;

	/**
	 * A named preference that controls whether kwyword are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_RETURN_BOLD = OvertureColorConstants.OVERTURE_KEYWORD_RETURN
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether keyword are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_KEYWORD_RETURN_ITALIC = OvertureColorConstants.OVERTURE_KEYWORD_RETURN
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
	public final static String EDITOR_KEYWORD_RETURN_STRIKETHROUGH = OvertureColorConstants.OVERTURE_KEYWORD_RETURN
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
	public final static String EDITOR_KEYWORD_RETURN_UNDERLINE = OvertureColorConstants.OVERTURE_KEYWORD_RETURN
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
	public final static String EDITOR_NUMBER_COLOR = OvertureColorConstants.OVERTURE_NUMBER;

	/**
	 * A named preference that controls whether number are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_NUMBER_BOLD = OvertureColorConstants.OVERTURE_NUMBER + EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether NUMBER are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_NUMBER_ITALIC = OvertureColorConstants.OVERTURE_NUMBER
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
	public final static String EDITOR_NUMBER_STRIKETHROUGH = OvertureColorConstants.OVERTURE_NUMBER
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

	public final static String EDITOR_NUMBER_UNDERLINE = OvertureColorConstants.OVERTURE_NUMBER
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
	public final static String EDITOR_STRING_COLOR = OvertureColorConstants.OVERTURE_STRING;

	/**
	 * A named preference that controls whether STRING are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in bold. If <code>false</code> the are rendered
	 * using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_STRING_BOLD = OvertureColorConstants.OVERTURE_STRING
			+ EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that controls whether STRING are rendered in italic.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in italic. If <code>false</code> the are rendered
	 * using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_STRING_ITALIC = OvertureColorConstants.OVERTURE_STRING
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
	public final static String EDITOR_STRING_STRIKETHROUGH = OvertureColorConstants.OVERTURE_STRING + EDITOR_STRIKETHROUGH_SUFFIX;

	/**
	 * A named preference that controls whether STRING are rendered in
	 * underline.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line
	 * comments are rendered in underline. If <code>false</code> the are
	 * rendered using no italic font style attribute.
	 * </p>
	 */
	public final static String EDITOR_STRING_UNDERLINE = OvertureColorConstants.OVERTURE_STRING + EDITOR_UNDERLINE_SUFFIX;

	public final static String EDITOR_FUNCTION_DEFINITION_COLOR = OvertureColorConstants.OVERTURE_FUNCTION_DEFINITION;

//	public static final String EDITOR_XML_TAG_NAME_COLOR = OvertureColorConstants.OVERTURE_XML_TAG_NAME;
//
//	public static final String EDITOR_XML_COMMENT_COLOR = OvertureColorConstants.OVERTURE_XML_COMMENT_NAME;
//
//	public static final String EDITOR_XML_BODY_ALL = OvertureColorConstants.OVERTURE_XML_ALL;
//
//	public static final String EDITOR_XML_ATTR_NAME_COLOR = OvertureColorConstants.OVERTURE_XML_ATTR_NAME;
//
//	private static final String EDITOR_XML_TAG_NAME_BOLD = OvertureColorConstants.OVERTURE_XML_TAG_NAME + EDITOR_BOLD_SUFFIX;
//
//	private static final String EDITOR_XML_ATTR_NAME_ITALIC = OvertureColorConstants.OVERTURE_XML_ATTR_NAME + EDITOR_ITALIC_SUFFIX;

	public final static String COMMENT_TASK_TAGS = OvertureColorConstants.OVERTURE_TODO_TAG;

	public final static String COMMENT_TASK_TAGS_BOLD = COMMENT_TASK_TAGS + EDITOR_BOLD_SUFFIX;
	
	public static final String OVERTURE_OPERATOR = OvertureColorConstants.OVERTURE_OPERATOR;
	public static final String OVERTURE_TYPE = OvertureColorConstants.OVERTURE_TYPE;
	public static final String OVERTURE_CONSTANT = OvertureColorConstants.OVERTURE_CONSTANT;
	public static final String OVERTURE_FUNCTION = OvertureColorConstants.OVERTURE_FUNCTION;
	public static final String OVERTURE_PREDICATE = OvertureColorConstants.OVERTURE_PREDICATE;
	
	
	public final static String OVERTURE_DIALECT_KEY = "VDM_DIALECT";
	
	
	public final static String OVERTURE_VDM_PLUS_PLUS = "VDM++";
	public final static String OVERTURE_VDM_PLUS_PLUS_REALTIME = "VDM++ RT";
	public final static String OVERTURE_VDM_SPECIFICATION_LANGUAGE = "VDM-SL";
	public final static String OVERTURE_OVERTURE_MODELLING_LANGUAGE = "OML";


	public static void initializeDefaultValues(IPreferenceStore store) {
		PreferenceConstants.initializeDefaultValues(store);

		PreferenceConverter.setDefault(store, OverturePreferenceConstants.OVERTURE_DOC_COLOR, new RGB(71, 102, 194));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR, new RGB(63, 127, 95));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_KEYWORD_COLOR, new RGB( 127, 0, 85));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_KEYWORD_RETURN_COLOR, new RGB(127, 0, 85));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_STRING_COLOR, new RGB(42, 0, 255));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_NUMBER_COLOR, new RGB(128, 0, 0));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_FUNCTION_DEFINITION_COLOR, new RGB(0, 0, 0));
//		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_XML_COMMENT_COLOR, new RGB(170, 200, 200));
//		PreferenceConverter.setDefault(store, OverturePreferenceConstants.EDITOR_XML_BODY_ALL, new RGB(240, 240, 240));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.OVERTURE_OPERATOR, new RGB(255, 0, 0));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.OVERTURE_TYPE, new RGB(0, 0, 255));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.OVERTURE_CONSTANT, new RGB(0, 0, 200));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.OVERTURE_FUNCTION, new RGB(0, 0, 255));
		PreferenceConverter.setDefault(store, OverturePreferenceConstants.OVERTURE_PREDICATE, new RGB(0, 0, 255));
		
		
		store.setDefault(OverturePreferenceConstants.OVERTURE_PREDICATE + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(OverturePreferenceConstants.OVERTURE_PREDICATE + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		store.setDefault(OverturePreferenceConstants.OVERTURE_FUNCTION + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(OverturePreferenceConstants.OVERTURE_FUNCTION + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		store.setDefault(OverturePreferenceConstants.OVERTURE_CONSTANT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(OverturePreferenceConstants.OVERTURE_CONSTANT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

		store.setDefault(OverturePreferenceConstants.OVERTURE_TYPE + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(OverturePreferenceConstants.OVERTURE_TYPE + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);		
		
		store.setDefault(OverturePreferenceConstants.OVERTURE_OPERATOR + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(OverturePreferenceConstants.OVERTURE_OPERATOR + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		
		store.setDefault(OverturePreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD, false);
		store.setDefault(OverturePreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_ITALIC, false);

		store.setDefault(OverturePreferenceConstants.EDITOR_KEYWORD_BOLD, true);
		store.setDefault(OverturePreferenceConstants.EDITOR_KEYWORD_ITALIC, false);
//		store.setDefault(OverturePreferenceConstants.EDITOR_XML_TAG_NAME_BOLD, true);
//		store.setDefault(OverturePreferenceConstants.EDITOR_XML_ATTR_NAME_ITALIC, true);
		store.setDefault(OverturePreferenceConstants.EDITOR_KEYWORD_RETURN_BOLD, true);
		store.setDefault(OverturePreferenceConstants.EDITOR_KEYWORD_RETURN_ITALIC, false);

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
