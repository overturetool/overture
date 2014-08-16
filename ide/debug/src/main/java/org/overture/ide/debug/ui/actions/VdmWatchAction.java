/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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

public class VdmWatchAction extends VdmEvaluationAction
{
	private void createWatchExpression(String snippet)
	{
		IWatchExpression expression = DebugPlugin.getDefault().getExpressionManager().newWatchExpression(snippet);
		DebugPlugin.getDefault().getExpressionManager().addExpression(expression);

		IAdaptable object = DebugUITools.getDebugContext();
		IDebugElement context = null;
		if (object instanceof IDebugElement)
		{
			context = (IDebugElement) object;
		} else if (object instanceof ILaunch)
		{
			context = ((ILaunch) object).getDebugTarget();
		}

		expression.setExpressionContext(context);
	}

	public void run()
	{
		Object selectedObject = getSelectedObject();

		if (selectedObject instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection) selectedObject;
			Iterator<?> elements = selection.iterator();
			while (elements.hasNext())
			{
				try
				{
					createWatchExpression(((IVdmVariable) elements.next()).getName());
				} catch (DebugException e)
				{
					VdmDebugPlugin.log(e);
					return;
				}
			}
		} else if (selectedObject instanceof String)
		{
			createWatchExpression((String) selectedObject);
		}

		showExpressionView();
	}
}
