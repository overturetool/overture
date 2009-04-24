/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text.completion;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;

public class OvertureContentAssistPreference extends ContentAssistPreference {
	static OvertureContentAssistPreference sDefault;
	protected ScriptTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}

	public static ContentAssistPreference getDefault() {
		if( sDefault == null ) {
			sDefault = new OvertureContentAssistPreference();
		}
		return sDefault;
	}
}
