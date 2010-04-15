package org.overture.ide.ui.navigator;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ResourceContainer extends Folder {

	private IFolder fFolder;
	
	public ResourceContainer(IFolder folder) {
		super(folder.getFullPath(),(Workspace) folder.getWorkspace());
		fFolder = folder;
	}

	

	public IFolder getFolder() {
		return fFolder;
	}



	public Collection<IResource> getChildren() {
		
		ArrayList<IResource> res = new ArrayList<IResource>();
		try {
			for (IResource iResource : fFolder.members()) {
				if(iResource instanceof IFolder){
					res.add(iResource);
				}
				else if (iResource instanceof IFile && VdmNavigatorContentProvider.isFileResource((IFile) iResource)){
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
		
		return ("ResourceContainer" + fFolder.getFullPath()).hashCode();
	}
	
}
