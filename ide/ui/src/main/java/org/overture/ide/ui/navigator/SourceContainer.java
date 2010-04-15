package org.overture.ide.ui.navigator;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Image;
import org.overture.ide.ui.VdmPluginImages;

public class SourceContainer extends Folder {

	private IFolder fFolder;

	private boolean isRoot = false;
	private boolean isDefault = false;

	public SourceContainer(IFolder folder, boolean isRoot) {
		super(folder.getFullPath(), (Workspace) folder.getWorkspace());
		this.isRoot = isRoot;
		setfFolder(folder);
	}

	public void setfFolder(IFolder fFolder) {
		this.fFolder = fFolder;
	}

	public IFolder getFolder() {
		return fFolder;
	}

	public String getText() {

		if (isDefault) {
			return "Default";
		}

		IPath path = fFolder.getProjectRelativePath();
		String[] segments = path.segments();
		StringBuffer s = new StringBuffer();

		for (int i = 0; i < segments.length - 1; i++) {
			s.append(segments[i]);
			s.append('.');
		}
		s.append(segments[segments.length - 1]);
		return s.toString();

	}

	public Image getImage() {
		if (isRoot) {
			return VdmPluginImages.get(VdmPluginImages.IMG_OBJS_PACKFRAG_ROOT);
		} else {
			return VdmPluginImages.get(VdmPluginImages.IMG_OBJS_PACKDECL);
		}

	}

	public boolean isRoot() {
		return isRoot;
	}

	private void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Collection<IResource> getChildren() {
		ArrayList<IResource> res = new ArrayList<IResource>();
		try {
			if (isRoot) {
				if (containsSources(fFolder)) {
					SourceContainer def = new SourceContainer(fFolder, false);
					def.setDefault(true);
					res.add(def);
				}
				res.addAll(childrenContainingSource(fFolder));

			} else {

				for (IResource iResource : fFolder.members()) {
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

		return ("SourceContainer" + fFolder.getFullPath() + isDefault + isRoot)
				.hashCode();
	}

	private ArrayList<SourceContainer> childrenContainingSource(IFolder folder) {

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

	private boolean containsSources(IFolder folder) {

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
}
