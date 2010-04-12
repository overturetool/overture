package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.navigator.resources.workbench.ResourceExtensionLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.overture.ide.core.IVdmProject;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmNavigatorLabelProvider extends ResourceExtensionLabelProvider implements ILabelProvider, IDescriptionProvider {

//	@Override
//	public String getText(Object element) {
//		String text = "";
//		if (IVdmProject.class.isInstance(element)) {
//			text = ((IVdmProject)element).getProjectName();
//		}
//		return text;
//
//	}
//	
//	@Override
//	public Image getImage(Object element) {
//		Image image = null;
//		if(IVdmProject.class.isInstance(element)) {
//			image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
//		}
//		else if(IResource.class.isInstance(element))
//		{
//			IResource resource = (IResource) element;
//			IContentTypeManager ctm = Platform.getContentTypeManager();
//			IContentType[] contentTypes = ctm.findContentTypesFor(resource.getName());
//
//			
//				
//			
//		}
//		
//		return image;
//	}
//
//	public String getDescription(Object anElement) {
//		// TODO Auto-generated method stub
//		return "description";
//	}

}
