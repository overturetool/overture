package org.overture.ide.core.resources;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.internal.core.ast.VdmModelManager;

public class AdapterFactoryNode implements IAdapterFactory
{

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType)
	{
		if (adapterType == IFile.class)
		{
			if (adaptableObject instanceof INode)
			{
				INode node = (INode) adaptableObject;

				//Search dept is here to protect against loops in the tree
				int searchDept = 5;
				while (--searchDept >0 && !(node instanceof SClassDefinition || node instanceof AModuleModules)
						&& (node = node.parent()) != null)
					;

				if (node instanceof SClassDefinition|| node instanceof AModuleModules)
				{
					return getIFileFromRootINode(node);
				}
			}
		}
		return null;
	}

	protected IFile getIFileFromRootINode(INode node)
	{
		List<IProject> projects = VdmModelManager.getInstance().getProjects();
		for (IProject iProject : projects)
		{
			IVdmProject p = (IVdmProject) iProject.getAdapter(IVdmProject.class);
			// we know this is ok, but we check anyway
			if (p != null)
			{
				IVdmModel model = p.getModel();
				if (model.getRootElementList().contains(node))
				{
					// ok we have the right project
					for (IVdmSourceUnit su : model.getSourceUnits())
					{
						if (su.getParseList().contains(node))
						{
							return su.getFile();
						}
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList()
	{
		return new Class[] { INode.class };
	}

}
