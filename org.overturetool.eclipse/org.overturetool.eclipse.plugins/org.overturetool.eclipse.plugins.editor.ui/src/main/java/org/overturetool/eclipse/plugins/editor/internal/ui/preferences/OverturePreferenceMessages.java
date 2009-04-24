/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;

public class OverturePreferenceMessages extends NLS {

	private static final String BUNDLE_NAME = EditorCoreUIConstants.OVERTURE_PREFERENCE_MESSAGES_BUNDLE_NAME;//"org.overturetool.internal.ui.preferences.OverturePreferenceMessages";//$NON-NLS-1$

	public static String OvertureEditorPreferencePage_general;
	public static String OvertureGlobalPreferencePage_description;
	public static String OvertureSmartTypingConfigurationBlock_smartPaste;
	public static String OvertureSmartTypingConfigurationBlock_typing_smartTab;
	public static String OvertureSmartTypingConfigurationBlock_closeStrings;
	public static String OvertureSmartTypingConfigurationBlock_closeBrackets;
	public static String OvertureSmartTypingConfigurationBlock_typing_tabTitle;
	public static String TodoTaskDescription;

	static {
		NLS.initializeMessages(BUNDLE_NAME, OverturePreferenceMessages.class);
	}
}
