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

					if (checkIfOnlyWarnings(markers))
					{
						Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
						decoration.addOverlay(DecorationOverlayIcon.createFromImage(image), IDecoration.BOTTOM_LEFT);
					} else
					{
						Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
						decoration.addOverlay(DecorationOverlayIcon.createFromImage(image), IDecoration.BOTTOM_LEFT);
					}
				}

			} catch (CoreException e)
			{
				VdmUIPlugin.log("Faild to decorate element: "+element, e);
			}

		}

	}

	private boolean checkIfOnlyWarnings(IMarker[] markers)
	{
		for (int i = 0; i < markers.length; i++)
		{
			try
			{
				Object tmp = markers[i].getAttribute(IMarker.SEVERITY);
				if (tmp.equals(IMarker.SEVERITY_ERROR))
				{
					return false;
				}
			} catch (CoreException e)
			{
				VdmUIPlugin.log("Faild to check marker attribute SEVERITY", e);
			}
		}
		return true;
	}

}
