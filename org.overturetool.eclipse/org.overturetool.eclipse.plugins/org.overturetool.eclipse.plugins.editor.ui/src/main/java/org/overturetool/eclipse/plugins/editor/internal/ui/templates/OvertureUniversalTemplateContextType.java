/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.templates;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.jface.text.IDocument;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;

public class OvertureUniversalTemplateContextType extends
		ScriptTemplateContextType {
	
	public static final String CONTEXT_TYPE_ID = EditorCoreUIConstants.CONTEXT_TYPE_ID;//"overtureUniversalTemplateContextType";

	public OvertureUniversalTemplateContextType() {
		// empty constructor
	}
	
	public OvertureUniversalTemplateContextType(String id) {
		super(id);
	}

	public OvertureUniversalTemplateContextType(String id, String name) {
		super(id, name);
	}

	public ScriptTemplateContext createContext(IDocument document, int offset, int length, ISourceModule sourceModule) {
		return new OvertureTemplateContext(this, document, offset, length, sourceModule);
	}
}
