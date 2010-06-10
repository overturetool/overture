/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/

package org.overture.ide.debug.ui.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.IVdmVariable;

public class VdmWatchAction extends VdmEvaluationAction {
	private void createWatchExpression(String snippet) {
		IWatchExpression expression = DebugPlugin.getDefault()
				.getExpressionManager().newWatchExpression(snippet);
		DebugPlugin.getDefault().getExpressionManager().addExpression(expression);
		
		IAdaptable object = DebugUITools.getDebugContext();
		IDebugElement context = null;
		if (object instanceof IDebugElement) {
			context = (IDebugElement) object;
		} else if (object instanceof ILaunch) {
			context = ((ILaunch) object).getDebugTarget();
		}
		
		expression.setExpressionContext(context);
	}

	public void run() {
		Object selectedObject = getSelectedObject();

		if (selectedObject instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) selectedObject;
			Iterator elements = selection.iterator();
			while (elements.hasNext()) {
				try {
					createWatchExpression(((IVdmVariable) elements.next())
							.getName());
				} catch (DebugException e) {
					VdmDebugPlugin.log(e);
					return;
				}
			}
		} else if (selectedObject instanceof String) {
			createWatchExpression((String) selectedObject);
		}

		showExpressionView();
	}
}
