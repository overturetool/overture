package org.overture.ide.ui.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.internal.viewsupport.ImportsContainer;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassInvariantDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.InheritedDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.modules.Import;
import org.overturetool.vdmj.modules.ImportFromModule;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleImports;
import org.overturetool.vdmj.types.RecordType;

public class VdmOutlineTreeContentProvider implements ITreeContentProvider
{

	// private TreeViewer fOutlineViewer;

	public VdmOutlineTreeContentProvider()
	{
		// this.fOutlineViewer = fOutlineViewer;
	}

	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof ClassDefinition)
		{
			// get definitions from the current class without inherited definitions
			DefinitionList defs = ((ClassDefinition) parentElement).definitions.singleDefinitions();
			//defs.addAll(((ClassDefinition) parentElement).localInheritedDefinitions);
			
			//defs = checkForThreads(defs);
			return filterDefinitionList(defs).toArray();

		} else if (parentElement instanceof Module)
		{
			// DefinitionList all = new DefinitionList();
			List<Object> all = new ArrayList<Object>();

			Module module = (Module) parentElement;
			
			if (module.imports != null)
			{
				all.add(new ImportsContainer(module.imports, module.importdefs));
			}
			all.addAll(filterDefinitionList(((Module) parentElement).defs.singleDefinitions()));
			filterSLModule(all);
			// all.addAll(((Module) parentElement).defs.singleDefinitions());
			return all.toArray();
		} else if (parentElement instanceof ModuleImports)
		{
			return ((ModuleImports) parentElement).imports.toArray();
		} else if (parentElement instanceof ImportsContainer){
			ImportsContainer container = (ImportsContainer) parentElement;
			if(!container.getImportDefs().isEmpty()){
				return container.getImportDefs().toArray();	
			}
			else{
				return container.getImports().imports.toArray();
			}
			
		}
		
		else if (parentElement instanceof ImportFromModule)
		{

			List<Object> all = new ArrayList<Object>();
			for (List<Import> iterable_element : ((ImportFromModule) parentElement).signatures)
			{
				all.addAll(iterable_element);
			}

			return all.toArray();
		} else if (parentElement instanceof TypeDefinition)
		{
			TypeDefinition typeDef = (TypeDefinition) parentElement;

			if (typeDef.type instanceof RecordType)
			{
				RecordType rType = (RecordType) typeDef.type;
				return rType.fields.toArray();
			}

		}

		return null;
	}

	private void filterSLModule(List<Object> all)
	{
		for (int i = 0; i < all.size(); i++)
		{
			if (all.get(i) instanceof LocalDefinition)
			{
				LocalDefinition localDef = (LocalDefinition) all.get(i);
				if (localDef.nameScope.name().equals("OLDSTATE"))
				{
					all.remove(i);
					i--;
				}

			}
		}

	}

//	private DefinitionList checkForThreads(DefinitionList defs)
//	{
//		for (int i = 0; i < defs.size(); i++)
//		{
//			Definition def = defs.get(i);
//
//			if (def != null)
//			{
//				if (def instanceof ThreadSupport)
//				{
//
//				} else
//				{
//					if (def instanceof ThreadDefinition)
//					{
//						ThreadDefinition eod = (ThreadDefinition) def;
//						if (eod.getName().equals("thread"))
//						{
//							defs.remove(i);
//							// defs.add(new ThreadSupport(eod));
//							i--;
//						}
//
//					}
//				}
//			}
//		}
//		return defs;
//	}

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
		} else if  (element instanceof ImportsContainer){
			return ((ImportsContainer) element).getImports().imports.size() > 0;
		}
		
		else if (element instanceof TypeDefinition)
		{
			TypeDefinition typeDef = (TypeDefinition) element;
			if (typeDef.type instanceof RecordType)
			{
				return ((RecordType) typeDef.type).fields.size() > 0;
			}
		}
		
		return false;
	}

	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IVdmSourceUnit)
		{
			IVdmSourceUnit node = (IVdmSourceUnit) inputElement;
			return node.getParseList().toArray();

		} else if (inputElement instanceof IVdmModel)
		{
			return (((IVdmModel) inputElement).getRootElementList()).toArray();
		}
		return new Object[0];
	}

	public void dispose()
	{

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{

	}

	private DefinitionList filterDefinitionList(DefinitionList fInput)
	{

		for (int i = 0; i < fInput.size(); i++)
		{

			Definition def = fInput.get(i);
			if (def != null)
			{
				try
				{

					def.hashCode();

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

//					if (def instanceof InheritedDefinition)
//					{
//						fInput.remove(i);
//						i--;
//					}

				} catch (NullPointerException e)
				{
					fInput.remove(i);
					i--;
				}
			} else
			{
				fInput.remove(i);
				i--;
			}

		}

		return fInput;
	}

}