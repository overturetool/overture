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

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.RulerBreakpointAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.overture.ide.debug.core.model.internal.VdmLineBreakpoint;

public class VdmBreakpointPropertiesRulerAction extends RulerBreakpointAction
		implements IUpdate
{

	private IBreakpoint fBreakpoint;

	public VdmBreakpointPropertiesRulerAction(ITextEditor editor,
			IVerticalRulerInfo info)
	{
		super(editor, info);
		setText("Breakpoint properties...");
	}

	/**
	 * @see Action#run()
	 */
	public void run()
	{
		if (getBreakpoint() != null)
		{
			PropertyDialogAction action = new PropertyDialogAction(getEditor().getEditorSite(), new ISelectionProvider()
			{
				public void addSelectionChangedListener(
						ISelectionChangedListener listener)
				{
				}

				public ISelection getSelection()
				{
					return new StructuredSelection(getBreakpoint());
				}

				public void removeSelectionChangedListener(
						ISelectionChangedListener listener)
				{
				}

				public void setSelection(ISelection selection)
				{
				}
			});
			action.run();
		}
	}

	/**
	 * @see IUpdate#update()
	 */
	public void update()
	{
		fBreakpoint = null;
		IBreakpoint breakpoint = getBreakpoint();
		if (breakpoint != null && breakpoint instanceof VdmLineBreakpoint)
		{
			fBreakpoint = breakpoint;
		}
		setEnabled(fBreakpoint != null);
	}

}
