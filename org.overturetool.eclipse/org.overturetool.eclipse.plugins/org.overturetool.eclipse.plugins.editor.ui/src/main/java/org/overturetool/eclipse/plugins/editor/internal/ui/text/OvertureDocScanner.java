/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;

public class OvertureDocScanner extends AbstractScriptScanner {

	private static String fgTokenProperties[] = new String[] { OvertureColorConstants.OVERTURE_DOC };

	public OvertureDocScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
	}

	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	@SuppressWarnings("unchecked")
	protected List createRules() {
		List rules = new ArrayList();
		IToken doc = getToken(OvertureColorConstants.OVERTURE_DOC);
		setDefaultReturnToken(doc);
		return rules;
	}
}
