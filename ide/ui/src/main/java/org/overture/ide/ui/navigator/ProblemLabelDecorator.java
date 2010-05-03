package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;

public class ProblemLabelDecorator extends BaseLabelProvider implements ILightweightLabelDecorator {
	

	public void decorate(Object element, IDecoration decoration) {		
		if (element instanceof IResource) {
			IResource resource = (IResource) element;
			try {
				IMarker[] markers = resource.findMarkers(IMarker.PROBLEM, true,
						IResource.DEPTH_INFINITE);
				if (markers.length > 0) {

					if (checkIfOnlyWarnings(markers)) {
						Image image = FieldDecorationRegistry.getDefault()
								.getFieldDecoration(
										FieldDecorationRegistry.DEC_WARNING)
								.getImage();
						decoration.addOverlay(DecorationOverlayIcon
								.createFromImage(image),
								IDecoration.BOTTOM_LEFT);
					} else {
						Image image = FieldDecorationRegistry.getDefault()
								.getFieldDecoration(
										FieldDecorationRegistry.DEC_ERROR)
								.getImage();
						decoration.addOverlay(DecorationOverlayIcon
								.createFromImage(image),
								IDecoration.BOTTOM_LEFT);
					}
				}

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private boolean checkIfOnlyWarnings(IMarker[] markers) {
		for (int i = 0; i < markers.length; i++) {
			try {
				if (markers[i].getAttribute(IMarker.SEVERITY).equals(
						IMarker.SEVERITY_ERROR))
					return false;
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}


}
