package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ide.core.utility.IVdmProject;
import org.overture.ide.core.utility.VdmProject;

public class VdmNavigatorContentProvider implements ITreeContentProvider {





	private static final Object[] NO_CHILDREN = {};
	private IVdmProject[] _vdmProjectParents;

	public Object[] getChildren(Object parentElement) {
		Object[] children = null;
		if (IWorkspaceRoot.class.isInstance(parentElement)) {
			if (_vdmProjectParents == null) {
				_vdmProjectParents = initializeParent(parentElement);
			}

			children = _vdmProjectParents;
		} else if(IVdmProject.class.isInstance(parentElement))
		{
			IVdmProject project = (IVdmProject) parentElement;
			try {
				children =  project.getProject().members();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			children = NO_CHILDREN;
		}

		return children;
	}


	private IVdmProject[] initializeParent(Object parentElement) {
		IProject [] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		IVdmProject[] result = new IVdmProject[projects.length];
		for (int i = 0; i < projects.length; i++) {
			result[i] = new VdmProject(projects[i]);
		}

		return result;
	}



	public Object getParent(Object element) {
		
		return null;
	}



	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if(element instanceof IVdmProject)
		{
			IVdmProject project = (IVdmProject) element;
			try {
				hasChildren =  project.getProject().members().length > 0;
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return hasChildren;
	}



	public Object[] getElements(Object inputElement) {
		
		return this.getChildren(inputElement);
	}



	public void dispose() {
		// TODO Auto-generated method stub
		
	}



	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}


}
