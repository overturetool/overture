package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.internal.navigator.resources.workbench.ResourceExtensionContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ide.core.VdmProject;

public class VdmNavigatorContentProvider extends ResourceExtensionContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object element) {
		 IWorkbenchAdapter adapter = getAdapter(element);
	        if (adapter != null) {
	            Object[] children = adapter.getChildren(element);
	            for (int i = 0; i <children.length ;i++)
				{
					if(children[i] instanceof IProject && VdmProject.isVdmProject((IProject) children[i]))
					{
					children[i] = VdmProject.createProject((IProject) children[i]);	
					}
				}
	            return children;
	        }
	        return new Object[0];
	}
//	private static final Object[] NO_CHILDREN = {};
//
//	public Object[] getChildren(Object parentElement) {
//		return null;
//		
//	}
//
//	public Object getParent(Object element) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public boolean hasChildren(Object element) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public Object[] getElements(Object inputElement) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public void dispose() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		// TODO Auto-generated method stub
//		
//	}
	
}
