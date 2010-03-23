package org.overture.ide.ui.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.internal.adaptor.IModel;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.IVdmSourceUnit;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassInvariantDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.modules.Import;
import org.overturetool.vdmj.modules.ImportFromModule;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleImports;

public class VdmOutlineTreeContentProvider implements ITreeContentProvider
{

	// private TreeViewer fOutlineViewer;

	public VdmOutlineTreeContentProvider() {
		// this.fOutlineViewer = fOutlineViewer;
	}

	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ClassDefinition)
		{

			DefinitionList defs = ((ClassDefinition) parentElement).getDefinitions();

			return filterDefinitionList(defs).toArray();

		} else if (parentElement instanceof Module)
		{

			// DefinitionList all = new DefinitionList();

			List<Object> all = new ArrayList<Object>();

			if (((Module) parentElement).imports != null)
			{
				all.add(((Module) parentElement).imports);
			}
			all.addAll(((Module) parentElement).defs.singleDefinitions());

			// all.addAll(((Module) parentElement).defs.singleDefinitions());
			return all.toArray();
		} else if (parentElement instanceof ModuleImports)
		{
			return ((ModuleImports) parentElement).imports.toArray();
		} else if (parentElement instanceof ImportFromModule)
		{

			List<Object> all = new ArrayList<Object>();
			for (List<Import> iterable_element : ((ImportFromModule) parentElement).signatures)
			{
				all.addAll(iterable_element);
			}

			return all.toArray();
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
		} else if (element instanceof Module)
		{
			return ((Module) element).defs.size() > 0;
		} else if (element instanceof ModuleImports)
		{
			return ((ModuleImports) element).imports.size() > 0;
		} else if (element instanceof ImportFromModule)
		{
			return ((ImportFromModule) element).signatures.size() > 0;
		}
		return false;
	}

	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IVdmSourceUnit)
		{
			IVdmSourceUnit node = (IVdmSourceUnit) inputElement;
			return node.getParseList().toArray();

		}else if(inputElement instanceof IVdmModel)
		{
			return (((IVdmModel) inputElement).getRootElementList()).toArray();
		}
		return new Object[0];
	}

	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{

	}

	private DefinitionList filterDefinitionList(DefinitionList fInput)
	{
		System.out.println("Size of input before filter: " + fInput.size());
		// Vector<Integer> index = new Vector<Integer>();
		for (int i = 0; i < fInput.size(); i++)
		{
			Definition def = fInput.get(i);
			if (def instanceof ClassInvariantDefinition)
			{

			}
			if (def instanceof ExplicitFunctionDefinition)
			{
				if (def.name.name.startsWith("pre_")
						|| def.name.name.startsWith("post_"))
				{
					fInput.remove(i);
					i--;
				}

			}
		}
		System.out.println("Size of input after filter: " + fInput.size());
		return fInput;
	}

}