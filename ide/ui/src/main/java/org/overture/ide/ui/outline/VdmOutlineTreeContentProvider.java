/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.SInvariantType;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.internal.viewsupport.ImportsContainer;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;

public class VdmOutlineTreeContentProvider implements ITreeContentProvider
{

	// private TreeViewer fOutlineViewer;

	public VdmOutlineTreeContentProvider()
	{
		// this.fOutlineViewer = fOutlineViewer;
	}

	public Object[] getChildren(Object parentElement)
	{
		TypeCheckerAssistantFactory factory = new TypeCheckerAssistantFactory();
		if (parentElement instanceof SClassDefinition)
		{
			// get definitions from the current class without inherited definitions
			List<PDefinition> defs = factory.createPDefinitionListAssistant().singleDefinitions(((SClassDefinition) parentElement).getDefinitions());
			// defs.addAll(((ClassDefinition) parentElement).localInheritedDefinitions);

			// defs = checkForThreads(defs);
			return filterDefinitionList(defs).toArray();

		} else if (parentElement instanceof AModuleModules)
		{
			// DefinitionList all = new DefinitionList();
			List<Object> all = new ArrayList<Object>();

			AModuleModules module = (AModuleModules) parentElement;

			if (module.getImports() != null)
			{
				all.add(new ImportsContainer(module.getImports(), module.getImportdefs()));
			}
			all.addAll(filterDefinitionList(factory.createPDefinitionListAssistant().singleDefinitions(((AModuleModules) parentElement).getDefs())));
			filterSLModule(all);
			// all.addAll(((Module) parentElement).defs.singleDefinitions());
			return all.toArray();
		} else if (parentElement instanceof AModuleImports)
		{
			return ((AModuleImports) parentElement).getImports().toArray();
		} else if (parentElement instanceof ImportsContainer)
		{
			ImportsContainer container = (ImportsContainer) parentElement;
			if (!container.getImportDefs().isEmpty())
			{
				return container.getImportDefs().toArray();
			} else
			{
				return container.getImports().getImports().toArray();
			}

		}

		else if (parentElement instanceof AFromModuleImports)
		{

			List<Object> all = new ArrayList<Object>();
			for (List<PImport> iterable_element : ((AFromModuleImports) parentElement).getSignatures())
			{
				all.addAll(iterable_element);
			}

			return all.toArray();
		} else if (parentElement instanceof ATypeDefinition)
		{
			ATypeDefinition typeDef = (ATypeDefinition) parentElement;
			SInvariantType type = typeDef.getInvType();

			if (type instanceof ARecordInvariantType)
			{
				ARecordInvariantType rType = (ARecordInvariantType) type;
				return rType.getFields().toArray();
			}

		}

		return null;
	}

	private void filterSLModule(List<Object> all)
	{
		for (int i = 0; i < all.size(); i++)
		{
			if (all.get(i) instanceof ALocalDefinition)
			{
				ALocalDefinition localDef = (ALocalDefinition) all.get(i);
				if (localDef.getNameScope().name().equals("OLDSTATE"))
				{
					all.remove(i);
					i--;
				}

			}
		}

	}

	// private DefinitionList checkForThreads(DefinitionList defs)
	// {
	// for (int i = 0; i < defs.size(); i++)
	// {
	// Definition def = defs.get(i);
	//
	// if (def != null)
	// {
	// if (def instanceof ThreadSupport)
	// {
	//
	// } else
	// {
	// if (def instanceof ThreadDefinition)
	// {
	// ThreadDefinition eod = (ThreadDefinition) def;
	// if (eod.getName().equals("thread"))
	// {
	// defs.remove(i);
	// // defs.add(new ThreadSupport(eod));
	// i--;
	// }
	//
	// }
	// }
	// }
	// }
	// return defs;
	// }

	public Object getParent(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element)
	{
		if (element instanceof SClassDefinition)
		{
			return ((SClassDefinition) element).getDefinitions().size() > 0;
		} else if (element instanceof AModuleModules)
		{
			return ((AModuleModules) element).getDefs().size() > 0;
		} else if (element instanceof AModuleImports)
		{
			return ((AModuleImports) element).getImports().size() > 0;
		} else if (element instanceof AFromModuleImports)
		{
			return ((AFromModuleImports) element).getSignatures().size() > 0;
		} else if (element instanceof ImportsContainer)
		{
			return ((ImportsContainer) element).getImports().getImports().size() > 0;
		}

		else if (element instanceof ATypeDefinition)
		{
			ATypeDefinition typeDef = (ATypeDefinition) element;
			SInvariantType type = typeDef.getInvType();
			if (type instanceof ARecordInvariantType)
			{
				return ((ARecordInvariantType) type).getFields().size() > 0;
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

	private List<PDefinition> filterDefinitionList(List<PDefinition> fInput)
	{

		for (int i = 0; i < fInput.size(); i++)
		{

			PDefinition def = fInput.get(i);
			if (def != null)
			{
				try
				{

					def.hashCode();

					if (def instanceof AClassInvariantDefinition)
					{

					}

					if (def instanceof AExplicitFunctionDefinition)
					{
						if (def.getName().getName().startsWith("pre_")
								|| def.getName().getName().startsWith("post_"))
						{
							fInput.remove(i);
							i--;
						}
					}

					// if (def instanceof InheritedDefinition)
					// {
					// fInput.remove(i);
					// i--;
					// }

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
