package org.overture.ide.ui.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.overture.ide.core.utility.IVdmProject;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmNavigatorLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	@Override
	public String getText(Object element) {
		String text = "";
		if (IVdmProject.class.isInstance(element)) {
			text = ((IVdmProject)element).getProjectName();
		}
		return text;

	}
	
	@Override
	public Image getImage(Object element) {
		Image image = null;
		if(IVdmProject.class.isInstance(element)) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
		}
		return image;
	}

	public String getDescription(Object anElement) {
		// TODO Auto-generated method stub
		return "description";
	}

}
