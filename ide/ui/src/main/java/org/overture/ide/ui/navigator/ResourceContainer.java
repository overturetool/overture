package org.overture.ide.ui.navigator;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ResourceContainer implements IWorkbenchAdapter, IVdmContainer {

	
	private IContainer fContainer;
//	
//	private IFolder fFolder;
//	private IProject fProject;

	public ResourceContainer(IFolder folder) {
		// super(folder.getFullPath(),(Workspace) folder.getWorkspace());
		//fFolder = folder;
		fContainer = folder;
	}

	public ResourceContainer(IProject element) {
		//this.fProject = element;
		fContainer = element;
	}

	

	public Collection<Object> getChildren() {

		ArrayList<Object> res = new ArrayList<Object>();
		

		try {
//			if (fProject != null) {
//				members = fProject.members();
//			} else {
//				members = fFolder.members();
//			}

			for (IResource iResource : fContainer.members()) {
				if (iResource instanceof IFolder) {
					res.add(new ResourceContainer((IFolder) iResource));
				} else if (iResource instanceof IFile
						&& VdmNavigatorContentProvider
								.isFileResource((IFile) iResource)) {
					res.add(iResource);
				}

			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int hashCode() {

		return ("ResourceContainer" + fContainer.getFullPath()).hashCode();
		
		
//		if(fProject != null){
//			return ("ResourceContainer" + fProject.getFullPath()).hashCode();
//		}
//		else{
//			return ("ResourceContainer" + fFolder.getFullPath()).hashCode();	
//		}
		
	}

	public Object[] getChildren(Object o) {
		return this.getChildren().toArray();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJ_FOLDER);
	}

	public String getLabel(Object o) {
		if (fContainer instanceof IProject) {
			return "Resources";
		} else {
			return fContainer.getFullPath().lastSegment();
		}
	}

	public Object getParent(Object o) {
		return fContainer.getParent();
	}

	public IContainer getContainer() {
		return fContainer;
	}

}
