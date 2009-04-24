/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.overturetool.eclipse.plugins.debug.ui.internal.console.ui.actions;

import org.eclipse.debug.core.DebugException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.debug.core.model.IScriptValue;
import org.eclipse.dltk.debug.core.model.IScriptVariable;
import org.eclipse.dltk.debug.ui.actions.ViewFilterAction;
import org.eclipse.jface.viewers.Viewer;

/**
 * Shows non-final static variables
 */
public class ShowFunctionsAction extends ViewFilterAction {

	public ShowFunctionsAction() {
		super();
	}

	protected String getPreferenceKey() {
		return "show_functions";
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IScriptVariable) {
			IScriptVariable variable = (IScriptVariable) element;
			try {
				return !((IScriptValue) variable.getValue()).getType()
						.getName().equals("function");
			} catch (DebugException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
