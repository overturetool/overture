package org.overture.ide.ui.navigator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.navigator.resources.workbench.ResourceExtensionContentProvider;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.core.resources.VdmProject;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmNavigatorContentProvider extends BaseWorkbenchContentProvider
		implements IResourceChangeListener, ITreeContentProvider {

	private TreeViewer fViewer;

	@Override
	public Object[] getElements(Object element) {
		ArrayList<IProject> result = new ArrayList<IProject>();

		IWorkbenchAdapter adapter = getAdapter(element);

		if (adapter != null) {
			Object[] children = adapter.getChildren(element);
			for (int i = 0; i < children.length; i++) {
				// TODO: kind of a hack so that closed projects still appear in
				// the navigator,
				// couldn't find work around since is possible to know a project
				// nature when it is closed. Java does the same way
				if (children[i] instanceof IProject) {
					IProject p = (IProject) children[i];
					if (!p.isOpen()) {
						result.add(p);
					} else {
						if (VdmProject.isVdmProject((IProject) children[i])) {
							result.add(VdmProject
									.createProject((IProject) children[i]));
						}
					}

				}

			}

			return result.toArray();
		}
		return new Object[0];
	}

	public void resourceChanged(IResourceChangeEvent event) {
		UIJob job = new UIJob(Workbench.getInstance().getDisplay(),
				"Navigator Update") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				TreeViewer viewer = fViewer;

				TreePath[] treePaths = viewer.getExpandedTreePaths();
				viewer.refresh();
				viewer.setExpandedTreePaths(treePaths);
				return new Status(IStatus.OK, "dsa", "Ok");
			}
		};

		job.schedule();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);

		fViewer = (TreeViewer) viewer;
		IWorkspace oldWorkspace = null;
		IWorkspace newWorkspace = null;

		if (oldInput instanceof IWorkspace) {
			oldWorkspace = (IWorkspace) oldInput;
		} else if (oldInput instanceof IContainer) {
			oldWorkspace = ((IContainer) oldInput).getWorkspace();
		}

		if (newInput instanceof IWorkspace) {
			newWorkspace = (IWorkspace) newInput;
		} else if (newInput instanceof IContainer) {
			newWorkspace = ((IContainer) newInput).getWorkspace();
		}

		if (oldWorkspace != newWorkspace) {
			if (oldWorkspace != null) {
				oldWorkspace.removeResourceChangeListener(this);
			}
			if (newWorkspace != null) {
				newWorkspace.addResourceChangeListener(this,
						IResourceChangeEvent.POST_CHANGE);
			}
		}

	}

	// private static final Object[] NO_CHILDREN = {};
	//
	public Object[] getChildren(Object element) {
		ArrayList<Object> result = new ArrayList<Object>();

		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter != null) {
			Object[] children = adapter.getChildren(element);
			for (Object object : children) {
				if (object instanceof IFolder) {
					if (containsResources((IFolder) object)) {
						result.add(new ResourceContainer((IFolder) object));
					}
					result.addAll(childrenContainingSource((IFolder) object));
				}
				if (object instanceof IFile) {
					result.add(object);
				}

			}

			return result.toArray();
		}
		if (element instanceof ResourceContainer) {
			ResourceContainer resourceContainer = (ResourceContainer) element;
			IFolder f = resourceContainer.getFolder();
			try {
				IResource[] members = f.members();
				for (IResource iResource : members) {

				}

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new Object[0];

	}

	//
	// public Object getParent(Object element) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;

	}

	private ArrayList<SourceContainer> childrenContainingSource(IFolder folder) {
		ArrayList<SourceContainer> result = new ArrayList<SourceContainer>();
		if (containsSources(folder)) {
			result.add(new SourceContainer(folder));
		}

		try {
			IResource[] resources = folder.members();
			for (IResource iResource : resources) {
				if (iResource instanceof IFolder) {
					result
							.addAll(childrenContainingSource((IFolder) iResource));
				}

			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
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

				if (iResource instanceof IFolder) {
					result = containsResources((IFolder) iResource);
				}

			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private boolean isFileSource(IFile iFile) {
		return !isFileResource(iFile);
	}

	private boolean isFileResource(IFile iResource) {
		String extention = ((IFile) iResource).getFileExtension();
		if (!extention.equals("vdmpp") && !extention.equals("vdmsl")
				&& !extention.equals("vdmrt"))
			return true;
		else
			return false;
	}

	private boolean containsResources(IFolder folder) {

		boolean result = false;

		try {
			IResource[] resources = folder.members();
			for (IResource iResource : resources) {
				if (iResource instanceof IFile) {

					if (isFileResource((IFile) iResource)) {
						result = true;
						break;
					}
				}

				if (iResource instanceof IFolder) {
					result = containsResources((IFolder) iResource);
				}

			}

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
