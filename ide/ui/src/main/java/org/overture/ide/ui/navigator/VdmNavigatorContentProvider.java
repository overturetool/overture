package org.overture.ide.ui.navigator;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmNavigatorContentProvider extends BaseWorkbenchContentProvider
		implements IResourceChangeListener, ITreeContentProvider
{

	private TreeViewer fViewer;

	@Override
	public Object[] getElements(Object element)
	{
		ArrayList<IProject> result = new ArrayList<IProject>();

		IWorkbenchAdapter adapter = getAdapter(element);

		if (adapter != null)
		{
			Object[] children = adapter.getChildren(element);
			for (int i = 0; i < children.length; i++)
			{
				if (children[i] instanceof IProject)
				{
					IProject p = (IProject) children[i];
					if (!p.isOpen())
					{
						result.add(p);
					} else
					{

						IVdmProject vdmProject = (IVdmProject) ((IProject) children[i]).getAdapter(IVdmProject.class);
						if (vdmProject != null)
						{
							result.add((IProject) children[i]);
						}
					}

				}

			}

			return result.toArray();
		}
		return new Object[0];
	}

	private UIJob jobNavigatorRefresh = new UIJob(PlatformUI.getWorkbench().getDisplay(), "Navigator Update (VdmNavigatorContent)")
	{
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor)
		{
			try
			{
				TreeViewer viewer = fViewer;
				if (!viewer.getControl().isDisposed())
				{
					viewer.refresh();
				}
				return Status.OK_STATUS;
			} catch (Exception e)
			{
				VdmUIPlugin.log(e);
				return new Status(IStatus.ERROR, IVdmUiConstants.PLUGIN_ID, "Error in Navigator Update (VdmNavigatorContent)", e);
			}
		}
	};

	public void resourceChanged(IResourceChangeEvent event)
	{
		jobNavigatorRefresh.schedule();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		super.inputChanged(viewer, oldInput, newInput);

		fViewer = (TreeViewer) viewer;
		IWorkspace oldWorkspace = null;
		IWorkspace newWorkspace = null;

		if (oldInput instanceof IWorkspace)
		{
			oldWorkspace = (IWorkspace) oldInput;
		} else if (oldInput instanceof IContainer)
		{
			oldWorkspace = ((IContainer) oldInput).getWorkspace();
		}

		if (newInput instanceof IWorkspace)
		{
			newWorkspace = (IWorkspace) newInput;
		} else if (newInput instanceof IContainer)
		{
			newWorkspace = ((IContainer) newInput).getWorkspace();
		}

		if (oldWorkspace != newWorkspace)
		{
			if (oldWorkspace != null)
			{
				oldWorkspace.removeResourceChangeListener(this);
			}
			if (newWorkspace != null)
			{
				newWorkspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
			}
		}

	}

	private static final Object[] NO_CHILDREN = {};

	public Object[] getChildren(Object element)
	{

		// if (element instanceof ResourceContainer) {
		// result
		// .addAll(getResourceContainerChildren((ResourceContainer) element));
		// // return result.toArray();
		// } else if (element instanceof SourceContainer) {
		// SourceContainer sourceContainer = (SourceContainer) element;
		// result.addAll(sourceContainer.getChildren());
		// // return result.toArray();
		// } else {

		// ODO: these lines should be commented to obtain the old navigator
		// START COMMENT HERE
		// if(element instanceof IProject){
		// ArrayList<Object> result = new ArrayList<Object>();
		// result.add(new SourceContainer((IProject) element));
		// result.add(new ResourceContainer((IProject) element));
		// return result.toArray();
		// }
		// END COMMENT HERE

		IWorkbenchAdapter adapter = getAdapter(element);

		if (adapter != null)
		{
			Object[] children = adapter.getChildren(element);
			return children;
			// for (Object object : children) {
			// if (object instanceof IFolder) {
			// result.addAll(getIFolderChildren((IFolder) object));
			//
			// }
			// if (object instanceof IFile) {
			//
			// result.add(object);
			//
			// }
			//
			// }
		}

		return NO_CHILDREN;
		// }

	}

//	private Collection<? extends Object> getIFolderChildren(IFolder folder)
//	{
//		ArrayList<Object> result = new ArrayList<Object>();
//
//		result.add(new ResourceContainer(folder));
//		if (isRootFolder(folder))
//		{
//			result.add(new SourceContainer(folder, true));
//		}
//		// result.addAll(childrenContainingSource((IFolder) object));
//
//		return result;
//	}
//
//	private ArrayList<Object> getResourceContainerChildren(
//			ResourceContainer element)
//	{
//		ArrayList<Object> result = new ArrayList<Object>();
//		ResourceContainer resourceContainer = (ResourceContainer) element;
//		for (Object object : resourceContainer.getChildren().toArray())
//		{
//			if (object instanceof IFolder)
//			{
//				result.add(new ResourceContainer((IFolder) object));
//			}
//			if (object instanceof IFile)
//			{
//				result.add(object);
//			}
//
//		}
//		return result;
//
//	}

	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;

	}

	static public boolean isRootFolder(IFolder folder)
	{
		return folder.getProjectRelativePath().toPortableString().equals("model");
	}

	static public boolean isFileResource(IFile iResource)
	{
		String extention = ((IFile) iResource).getFileExtension();
		if (!extention.equals("vdmpp") && !extention.equals("vdmsl")
				&& !extention.equals("vdmrt"))
			return true;
		else
			return false;
	}

}
