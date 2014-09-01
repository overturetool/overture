/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.overture.ide.ui.VdmUIPlugin;

public class ProblemLabelDecorator extends BaseLabelProvider implements
		ILightweightLabelDecorator
{

	public void decorate(Object element, IDecoration decoration)
	{
		if (element instanceof IResource)
		{
			IResource resource = (IResource) element;
			if (resource instanceof IProject)
			{
				if (!((IProject) resource).isOpen())
					return;
			}

			try
			{
				if(!resource.exists())
				{
					return;//Nothing to do then.
				}
				IMarker[] markers = resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				if (markers.length > 0)
				{
					if(containsSeverity(markers,IMarker.SEVERITY_ERROR))
					{
						Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
						decoration.addOverlay(DecorationOverlayIcon.createFromImage(image), IDecoration.BOTTOM_LEFT);
					}else if(containsSeverity(markers,IMarker.SEVERITY_WARNING))
					{
						Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
						decoration.addOverlay(DecorationOverlayIcon.createFromImage(image), IDecoration.BOTTOM_LEFT);
					} 
				}

			} catch (CoreException e)
			{
				VdmUIPlugin.log("Faild to decorate element: "+element, e);
			}

		}

	}

	/**
	 * Checks if any marker in the array has a defined severity which is set to the 
	 * @param markers
	 * @param severity
	 * @return
	 */
	private boolean containsSeverity(IMarker[] markers, int severity)
	{
		for (int i = 0; i < markers.length; i++)
		{
			try
			{
				Object tmp = markers[i].getAttribute(IMarker.SEVERITY);
				if(tmp == null && !(tmp instanceof Integer))
				{
					continue;
				}
				if (tmp!=null && ((Integer)tmp & severity)!=0)
				{
					return true;
				}
			} catch (CoreException e)
			{
				VdmUIPlugin.log("Faild to check marker attribute SEVERITY", e);
			}
		}
		return false;
	}

}
