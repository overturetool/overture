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
package org.overture.ide.debug.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.overture.ide.debug.core.model.internal.VdmValue;

public class VdmDebugModelPresentation extends LabelProvider implements
		IDebugModelPresentation
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue,
	 * org.eclipse.debug.ui.IValueDetailListener)
	 */
	public void computeDetail(IValue value, IValueDetailListener listener)
	{
		String detail = "";
		try
		{
			if (value instanceof VdmValue)
			{
				VdmValue vdmValue = (VdmValue) value;
				vdmValue.getVariables();
				detail = vdmValue.getRawValue();
			} else
			{
				detail = value.getValueString();
			}

		} catch (DebugException e)
		{
		}
		listener.detailComputed(value, detail);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	public String getEditorId(IEditorInput input, Object element)
	{
		if (element instanceof ILineBreakpoint)
		{
			ILineBreakpoint breakpoint = (ILineBreakpoint) element;
			element = breakpoint.getMarker().getResource();
		}

		if (element instanceof IFile)
		{
			IFile file = (IFile) element;
			try
			{
				String contentTypeId = file.getContentDescription().getContentType().getId();
				if (SourceViewerEditorManager.getInstance().getContentTypeIds().contains(contentTypeId))
				{
					return SourceViewerEditorManager.getInstance().getEditorId(contentTypeId);
				} else
				// TODO: it fixes the source lookup for wordDocs - not sure if this has other implications
				{
					return SourceViewerEditorManager.getInstance().getEditorId("org.overture.ide.vdm.external.content-type");
				}
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// return "org.overture.ide.vdmpp.ui.editor";
		}
		return null;
	}

	public IEditorInput getEditorInput(Object element)
	{
		if (element instanceof IFile)
		{
			return new FileEditorInput((IFile) element);
		}
		if (element instanceof ILineBreakpoint)
		{
			return new FileEditorInput((IFile) ((ILineBreakpoint) element).getMarker().getResource());
		}
		return null;
	}

	public void setAttribute(String attribute, Object value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getText(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
