package org.overture.ide.ui.navigator;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ide.ui.VdmPluginImages;

public class SourceContainer implements IWorkbenchAdapter, IVdmContainer {

	private IContainer fContainer = null;
	private boolean isRoot = false;
	private boolean isDefault = false;

	public SourceContainer(IFolder folder, boolean isRoot) {

		this.isRoot = isRoot;
		fContainer = folder;
	}

	public SourceContainer(IProject element) {
		fContainer = element;
	}

	public boolean isRoot() {
		return isRoot;
	}

	private void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Collection<Object> getChildren() {
		ArrayList<Object> res = new ArrayList<Object>();
		try {
			if (fContainer instanceof IProject && isDefault) {
				for (IResource iResource : fContainer.members()) {
					if (iResource instanceof IFile
							&& isFileSource((IFile) iResource)) {
						res.add(iResource);
					}

				}

			} else 
			
			if (fContainer instanceof IProject) {
				if (containsSources(fContainer)) {
					SourceContainer def = new SourceContainer((IProject)fContainer);
					def.setDefault(true);
					res.add(def);
				}
				res.addAll(childrenContainingSource(fContainer));

			} else {
				for (IResource iResource : fContainer.members()) {
					if (iResource instanceof IFile
							&& isFileSource((IFile) iResource)) {
						res.add(iResource);
					}

				}
				
			}
		} catch (CoreException e) {
			return res;
		}
		return res;
	}

	@Override
	public int hashCode() {

		return ("SourceContainer" + fContainer.getFullPath() + isDefault + isRoot)
				.hashCode();
		// if(fProject != null){
		//			
		// }
		// else{
		// return ("SourceContainer" + fFolder.getFullPath() + isDefault +
		// isRoot)
		// .hashCode();
		// }

	}

	private ArrayList<SourceContainer> childrenContainingSource(IContainer folder) {

		ArrayList<SourceContainer> result = new ArrayList<SourceContainer>();

		try {
			IResource[] resources = folder.members();
			for (IResource iResource : resources) {
				if (iResource instanceof IFolder) {
					if (containsSources((IFolder) iResource)) {
						result.add(new SourceContainer((IFolder) iResource,
								false));
					}
					result
							.addAll(childrenContainingSource((IFolder) iResource));
				}
			}

		} catch (CoreException e) {

			e.printStackTrace();
		}

		return result;
	}

	private boolean containsSources(IContainer folder) {

		boolean result = false;

		try {
			IResource[] resources = folder.members();
			for (IResource iResource : resources) {
				if (iResource instanceof IFile) {
					if (isFileSource((IFile) iResource)) {
						result = true;
						break;
					}
				}

				// if (iResource instanceof IFolder) {
				// result = containsSources((IFolder) iResource);
				// }

			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private boolean isFileSource(IFile iFile) {
		return !VdmNavigatorContentProvider.isFileResource(iFile);
	}

	public Object[] getChildren(Object o) {
		return this.getChildren().toArray();
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		
		
		if (fContainer instanceof IProject && !isDefault) {
			return VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_OBJS_PACKFRAG_ROOT);
		} else {
			return VdmPluginImages
					.getDescriptor(VdmPluginImages.IMG_OBJS_PACKAGE);
		}
	}

	public String getLabel(Object o) {
		if (fContainer instanceof IProject && isDefault) {
			return "Default";
		}
		
		if (fContainer instanceof IProject) {
			return "Model";
		}

		

		IPath path = fContainer.getProjectRelativePath();
		String[] segments = path.segments();
		StringBuffer s = new StringBuffer();

		for (int i = 0; i < segments.length - 1; i++) {
			s.append(segments[i]);
			s.append('.');
		}
		s.append(segments[segments.length - 1]);
		return s.toString();
	}

	public Object getParent(Object o) {
		return fContainer.getParent();
	}

	public IContainer getContainer() {
		return fContainer;
	}
}
