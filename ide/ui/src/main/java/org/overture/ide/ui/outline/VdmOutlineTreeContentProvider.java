package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.ast.RootNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.modules.Module;

public class VdmOutlineTreeContentProvider implements ITreeContentProvider
{

	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ClassDefinition)
		{
			return ((ClassDefinition) parentElement).getDefinitions().toArray();
		}else if (parentElement instanceof Module)
		{
			return ((Module) parentElement).defs.toArray();
		}
		return null;
	}

	public Object getParent(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element)
	{
		if (element instanceof ClassDefinition)
		{
			return ((ClassDefinition) element).getDefinitions().size() > 0;
		}else if (element instanceof Module)
		{
			return ((Module) element).defs.size() > 0;
		}
		return false;
	}

	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof RootNode)
			try
			{
				RootNode node = (RootNode) inputElement;
				if (node.hasClassList())
					return node.getClassList().toArray();
				else if (node.hasModuleList())
					return node.getModuleList().toArray();
			} catch (NotAllowedException e)
			{
				e.printStackTrace();
			}
		return null;
	}

	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// TODO Auto-generated method stub

	}

}