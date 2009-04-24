/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.search;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.search.ScriptSearchPage;
import org.overturetool.eclipse.plugins.editor.core.OvertureLanguageToolkit;

public class OvertureSearchPage extends ScriptSearchPage {
	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return OvertureLanguageToolkit.getDefault();
	}
}
