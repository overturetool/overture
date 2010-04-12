package org.overture.ide.ui.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.internal.navigator.resources.workbench.ResourceExtensionLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;

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
