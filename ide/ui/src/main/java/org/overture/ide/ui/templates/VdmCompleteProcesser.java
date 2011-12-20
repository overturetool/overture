/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.templates;

import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;
import org.overture.ide.ui.templates.VdmContentAssistProcessor.VdmCompletionContext;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.modules.Module;

public class VdmCompleteProcesser
{
	VdmElementImageProvider imgProvider = new VdmElementImageProvider();

	public void computeCompletionProposals(VdmCompletionContext info,
			VdmDocument document, List<ICompletionProposal> proposals,
			int offset)
	{

		if (info.afterNew || info.afterMk || info.isEmpty)
		{
			completeTypes(info, document, proposals, offset);
		} else
		{
			completeFields(info, document, proposals, offset);
		}
	}

	public void completeTypes(VdmCompletionContext info, VdmDocument document,
			List<ICompletionProposal> proposals, int offset)
	{
		boolean modulesOnly = info.afterNew || info.isEmpty;
		boolean recordTypesOnly = info.afterMk || info.isEmpty;
		String typeName = info.field.toString();

		for (IAstNode element : getAst(document))
		{
			if (modulesOnly)
			{
				String name = element.getName();
				if (name.startsWith(typeName) || name.length() == 0)
				{
					IContextInformation ctxtInfo = new ContextInformation(name, name); //$NON-NLS-1$
					proposals.add(new CompletionProposal(name, offset, 0, name.length(), imgProvider.getImageLabel(element, 0), name, ctxtInfo, name));
				}
			}
			addContainerTypes(element, recordTypesOnly, offset, proposals);

		}
	}

	private void addContainerTypes(IAstNode def, boolean recordTypesOnly,
			int offset, List<ICompletionProposal> proposals)
	{
		if (def instanceof ClassDefinition)
		{
			ClassDefinition cd = (ClassDefinition) def;
			for (Definition element : cd.getDefinitions())
			{
				if (element instanceof TypeDefinition)
				{
					String name = cd.getName() + "`" + element.getName();
					IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$
					proposals.add(new CompletionProposal(name, offset, 0, name.length(), imgProvider.getImageLabel(element, 0), name, info, name));
				}
			}
		} else if (def instanceof Module)
		{
			Module m = (Module) def;
			for (Definition element : m.defs)
			{
				if (element instanceof TypeDefinition)
				{
					String name = m.getName() + "`" + element.getName();
					IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$
					proposals.add(new CompletionProposal(name, offset, 0, name.length(), imgProvider.getImageLabel(element, 0), name, info, name));
				}
			}
		}

	}

	public void completeFields(VdmCompletionContext info, VdmDocument document,
			List<ICompletionProposal> proposals, int offset)
	{
		try
		{
			List<IAstNode> ast = getAst(document);
			if (info.fieldType.toString().trim().length() != 0)
			{
				completeFromType(info.fieldType.toString(), info.proposal.toString(), proposals, offset, ast);
			} else
			{
				List<IAstNode> possibleMatch = new Vector<IAstNode>();
				for (IAstNode node : getLocalFileAst(document))
				{
					for (IAstNode field : getFields(node))
					{
						if (field.getName().equals(info.field.toString()))
						{
							// Ok match then complete it
							completeFromType(getTypeName(field), info.proposal.toString(), proposals, offset, ast);
						} else if (field.getName().startsWith(info.field.toString()))
						{
							possibleMatch.add(field);
						}

					}

				}
			}
		} catch (Exception e)
		{
			VdmUIPlugin.log("Completion error in " + getClass().getSimpleName(), e);
		}
	}

	private String getTypeName(IAstNode field)
	{
		if (field instanceof InstanceVariableDefinition)
		{
			return ((InstanceVariableDefinition) field).type.toString();
		}
		return "";
	}

	private void completeFromType(String typeName, String proposal,
			List<ICompletionProposal> proposals, int offset, List<IAstNode> ast)
	{
		System.out.println("Complete for type: " + typeName
				+ " with proposal: " + proposal);
		IAstNode type = getType(typeName, ast);

		// Fields
		for (IAstNode field : getFields(type))
		{
			if (field.getName().startsWith(proposal) || proposal.isEmpty())
			{
				proposals.add(createProposal(field, offset));
			}
		}
		// Operations
		for (IAstNode op : getOperations(type))
		{
			if (op.getName().startsWith(proposal) || proposal.isEmpty())
			{
				proposals.add(createProposal(op, offset));
			}
		}
		// Functions
		for (IAstNode fn : getFunctions(type))
		{
			if (fn.getName().startsWith(proposal) || proposal.isEmpty())
			{
				proposals.add(createProposal(fn, offset));
			}
		}
		// Types
		for (IAstNode tp : getTypes(type))
		{
			if (tp.getName().startsWith(proposal) || proposal.isEmpty())
			{
				proposals.add(createProposal(tp, offset));
			}
		}
	}

	private ICompletionProposal createProposal(IAstNode node, int offset)
	{
		String name = node.getName();
		if (node instanceof TypeDefinition)
		{
			name = node.getLocation().module + "`" + name;
		}
		IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$
		return new CompletionProposal(name, offset, 0, name.length(), imgProvider.getImageLabel(node, 0), name, info, name);
	}

	private IAstNode getType(String typeName, List<IAstNode> ast)
	{
		for (IAstNode node : ast)
		{
			if (node.getName().equals(typeName))
			{
				return node;
			}
		}
		return null;
	}

	private List<IAstNode> getFields(IAstNode node)
	{
		List<IAstNode> fields = new Vector<IAstNode>();
		DefinitionList list = getDefinitions(node);

		if (list != null)
		{
			for (Definition definition : list)
			{
				if (definition instanceof LocalDefinition
						|| definition instanceof ValueDefinition
						|| definition instanceof InstanceVariableDefinition)
				{
					fields.add(definition);
				}
			}
		}
		return fields;
	}

	private List<IAstNode> getTypes(IAstNode node)
	{
		List<IAstNode> types = new Vector<IAstNode>();
		DefinitionList list = getDefinitions(node);
		if (list != null)
		{
			for (Definition definition : list)
			{
				if (definition instanceof TypeDefinition)
				{
					types.add(definition);
				}
			}
		}
		return types;
	}

	private List<IAstNode> getOperations(IAstNode node)
	{
		List<IAstNode> ops = new Vector<IAstNode>();
		DefinitionList list = getDefinitions(node);

		if (list != null)
		{
			for (Definition definition : list)
			{
				if (definition instanceof ExplicitOperationDefinition
						|| definition instanceof ImplicitOperationDefinition)
				{
					ops.add(definition);
				}
			}
		}
		return ops;
	}

	private List<IAstNode> getFunctions(IAstNode node)
	{
		List<IAstNode> fns = new Vector<IAstNode>();
		DefinitionList list = getDefinitions(node);

		if (list != null)
		{
			for (Definition definition : list)
			{
				if (definition instanceof ExplicitFunctionDefinition
						|| definition instanceof ImplicitFunctionDefinition)
				{
					fns.add(definition);
				}
			}
		}
		return fns;
	}

	private DefinitionList getDefinitions(IAstNode node)
	{
		DefinitionList list = null;
		if (node instanceof ClassDefinition)
		{
			list = ((ClassDefinition) node).getDefinitions();
		} else if (node instanceof Module)
		{
			list = ((Module) node).defs;
		}
		return list;
	}

	private List<IAstNode> getAst(VdmDocument document)
	{
		List<IAstNode> ast = new Vector<IAstNode>();
		ast.addAll(document.getProject().getModel().getRootElementList());
		ast.addAll(document.getSourceUnit().getParseList());
		return ast;
	}

	private List<IAstNode> getLocalFileAst(VdmDocument document)
	{
		List<IAstNode> ast = new Vector<IAstNode>();
		ast.addAll(document.getSourceUnit().getParseList());
		return ast;
	}
}
