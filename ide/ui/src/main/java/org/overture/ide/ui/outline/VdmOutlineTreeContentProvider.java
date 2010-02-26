package org.overture.ide.ui.outline;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.ast.RootNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.DefinitionList;
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
				{
					if(!node.getModuleList().isEmpty() && node.getModuleList().get(0).name.name.equals("DEFAULT"))
					{
						DefinitionList definitions = new DefinitionList();
						
						for(Module m : node.getModuleList())
						{
							definitions.addAll(m.defs);
						}
						
						
						Module module = new Module(new File("mergedFile"),definitions);
						return new Object[]{module};
					}
					else
						return node.getModuleList().toArray();
				}
			} catch (NotAllowedException e)
			{
				e.printStackTrace();
			}
		return new Object[0];
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