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
package org.overture.ide.ui.templates;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.templates.TemplateContext;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2.TextReference;
import org.overture.ide.ui.utility.ast.AstNameUtil;

public class VdmCompleteProcessor
{
	VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	private static VdmCompletionExtractor vdmCompletionExtractor = new VdmCompletionExtractor();
	private static VdmCompletionHelper VdmHelper = new VdmCompletionHelper();
	
	public void computeCompletionProposals(VdmCompletionContext info,
			VdmDocument document, List<ICompletionProposal> proposals,
			int offset, ITextViewer viewer,TemplateContext context)
	{
		List<ICompletionProposal> calculatedProposals = new Vector<ICompletionProposal>();
		// if (info.afterNew || info.afterMk || info.isEmpty)
		// {
		// completeTypes(info, document, calculatedProposals, offset);
		// } else
		// {
		// completeFields(info, document, calculatedProposals, offset);
		// completeFields(info, document, calculatedProposals, offset);
		

		
		switch (info.type)
		{
			case CallParam:
				break;
			case Dot:
				break;
			case Mk:
				break;
			case New:
				completeNew(info, document, calculatedProposals, offset);
				break;
			case Quote:
				completeQuotes(info, document, calculatedProposals, offset);
				break;
			case Types:
				completeTypes(info, document, calculatedProposals, offset);
				vdmCompletionExtractor.completeBasicTypes(info, document, calculatedProposals, offset, getAst(document), context,viewer);
				break;
			default:
				break;

		}

		List<String> replacementDisplayString = new Vector<String>();
		for (ICompletionProposal proposal : calculatedProposals)
		{
			if (proposal instanceof CompletionProposal)
			{
				CompletionProposal cp = (CompletionProposal) proposal;
				if (replacementDisplayString.contains(cp.getDisplayString())
				// || !cp.getDisplayString().toLowerCase().replace('<',
				// ' ').trim().startsWith(info.prefix.toString().toLowerCase())
				)
				{
					continue;
				}
				replacementDisplayString.add(cp.getDisplayString());
			}
			proposals.add(proposal);

		}
	}

	private void completeQuotes(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			int offset)
	{
		for (INode def : getAst(document))
		{
			completeQuotes(offset, proposals, info, def);
		}

	}

	private void completeNew(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			final int offset)
	{
		for (INode container : getAst(document))
		{
			try
			{
				container.apply(new DepthFirstAnalysisAdaptor()
				{
					@Override
					public void caseAExplicitOperationDefinition(
							AExplicitOperationDefinition node)
							throws AnalysisException
					{

						if (node.getIsConstructor()
								&& new PAccessSpecifierAssistant(null).isPublic(node.getAccess()))
						{
							String name = node.getName().getName();
							if (info.proposalPrefix.isEmpty()
									|| name.toLowerCase().startsWith(info.proposalPrefix.toLowerCase()))
							{
								IContextInformation infoComplete = new ContextInformation(name, name);

								String replacementString = name + "(";

								for (Iterator<PPattern> iterator = node.getParameterPatterns().iterator(); iterator.hasNext();)
								{
									PPattern pattern = iterator.next();

									replacementString += pattern.toString();
									if (iterator.hasNext())
										replacementString += ", ";

								}
								replacementString += ")";

								proposals.add(new CompletionProposal(replacementString, offset
										+ info.offset, info.proposalPrefix.length(), replacementString.length(), imgProvider.getImageLabel(node, 0), replacementString, infoComplete, node.toString()));
							}
						}
					}
				});
			} catch (AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void completeTypes(VdmCompletionContext info, VdmDocument document,
			List<ICompletionProposal> proposals, int offset)
	{
		// boolean modulesOnly = info.afterNew || info.isEmpty;
		// boolean recordTypesOnly = info.afterMk || info.isEmpty;
		// String typeName = info.field.toString();

		for (INode element : getAst(document))
		{
			if (info.type == SearchType.Types)
			{
				String name = AstNameUtil.getName(element);
				
				if (VdmHelper.findInString(info.proposalPrefix,name))
				{
					IContextInformation ctxtInfo = new ContextInformation(name, name); //$NON-NLS-1$
					proposals.add(new CompletionProposal(name, offset, 0, name.length(), imgProvider.getImageLabel(element, 0), name, ctxtInfo, name));
				}
			}
			addContainerTypes(element, offset, proposals, info);

		}
	}

	private void addContainerTypes(INode def, final int offset,
			final List<ICompletionProposal> proposals,
			final VdmCompletionContext info2)
	{
		if (def instanceof SClassDefinition)
		{
			SClassDefinition cd = (SClassDefinition) def;
			for (PDefinition element : cd.getDefinitions())
			{
				if (element instanceof ATypeDefinition)
				{
					String name = cd.getName() + "`" + element.getName();
					IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$
					proposals.add(new CompletionProposal(name, offset, 0, name.length(), imgProvider.getImageLabel(element, 0), name, info, name));
				}

			}
			completeQuotes(offset, proposals, info2, def);
		} else if (def instanceof AModuleModules)
		{
			AModuleModules m = (AModuleModules) def;
			for (PDefinition element : m.getDefs())
			{
				String prefix = "";
				if (element.getAncestor(AModuleModules.class) != def)
				{
					prefix = m.getName() + "`";
				}

				if (element instanceof ATypeDefinition)
				{
					String name = prefix + element.getName();
					IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$

					if (name.toLowerCase().startsWith(info2.proposalPrefix.toString().toLowerCase()))
					{
						proposals.add(new CompletionProposal(name, offset
								- info2.proposalPrefix.length(), info2.proposalPrefix.length(), name.length(), imgProvider.getImageLabel(element, 0), name, info, element.toString()));
					}
				}
			}

			completeQuotes(offset, proposals, info2, m);
		}

	}
	
	private void completeQuotes(final int offset,
			final List<ICompletionProposal> proposals,
			final VdmCompletionContext info2, INode m)
	{
		try
		{
			m.apply(new DepthFirstAnalysisAdaptor()
			{
				@Override
				public void caseAQuoteLiteralExp(AQuoteLiteralExp node)
						throws AnalysisException
				{
					populateQuotes(node, node.getValue().getValue(), node.toString());
				}

				@Override
				public void caseAQuoteType(AQuoteType node)
						throws AnalysisException
				{
					populateQuotes(node, node.getValue().getValue(), node.toString());
				}

				void populateQuotes(INode node, String baseValue, String name)
				{
					// if (!info2.prefix.toString().equals(baseValue))
					{

						IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$

						int curOffset = offset + info2.offset;// - info2.proposalPrefix.length();
						int length = name.length();
						int replacementLength = info2.proposalPrefix.length();

						if (info2.proposalPrefix.toString().equals("<"
								+ baseValue + ">"))
						{
							// replacementLength+=1;
							// length+=1;
							curOffset = offset;
							replacementLength = 0;
						}

						if (("<" + baseValue).toLowerCase().startsWith(info2.proposalPrefix.toString().toLowerCase()))
						{
							proposals.add(new CompletionProposal(name, curOffset, replacementLength, length, imgProvider.getImageLabel(node, 0), name, info, name));
						}
					}
				}
			});
		} catch (AnalysisException e)
		{
			VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
					+ "faild during quote search", e);
		}
	}

	public void completeFields(VdmCompletionContext info, VdmDocument document,
			List<ICompletionProposal> proposals, int offset)
	{
		try
		{
			List<INode> ast = getAst(document);

			INode found = new AstLocationSearcher2().getNode(new TextReference(document.getSourceUnit().getSystemFile(), offset), getLocalFileAst(document));

			if (found != null)
			{
				PType type = null;
				if (found instanceof PExp)
				{
					type = ((PExp) found).getType();
				} else if (found instanceof PStm)
				{
					type = ((PStm) found).getType();
				}

				if (type instanceof ARecordInvariantType)
				{
					ARecordInvariantType rt = (ARecordInvariantType) type;

					for (AFieldField field : rt.getFields())
					{
						if (field.getTag().toLowerCase().startsWith(info.proposalPrefix.toString().toLowerCase()))
						{
							proposals.add(createProposal(field, offset, info));
						}
					}
				}
			} else
			{
				// FIXME old code

				// if (info.fieldType.toString().trim().length() != 0)
				// {
				// completeFromType(info.fieldType.toString(), info.proposalPrefix.toString(), proposals, offset, ast);
				// } else
				// {
				// List<INode> possibleMatch = new Vector<INode>();
				// for (INode node : getLocalFileAst(document))
				// {
				// for (INode field : getFields(node))
				// {
				// if (AstNameUtil.getName(field).equals(info.field.toString()))
				// {
				// // Ok match then complete it
				// completeFromType(getTypeName(field), info.proposalPrefix.toString(), proposals, offset, ast);
				// } else if (AstNameUtil.getName(field).startsWith(info.field.toString()))
				// {
				// possibleMatch.add(field);
				// }
				//
				// }
				//
				// }
				// }
			}
		} catch (Exception e)
		{
			VdmUIPlugin.log("Completion error in " + getClass().getSimpleName(), e);
		}
	}

	private String getTypeName(INode field)
	{
		if (field instanceof AInstanceVariableDefinition)
		{
			return ((AInstanceVariableDefinition) field).getType().toString();
		}
		return "";
	}

	private void completeFromType(String typeName, String proposal,
			List<ICompletionProposal> proposals, int offset, List<INode> ast)
	{
		// System.out.println("Complete for type: " + typeName
		// + " with proposal: " + proposal);
		INode type = getType(typeName, ast);

		// Fields
		for (INode field : getFields(type))
		{
			if (AstNameUtil.getName(field).startsWith(proposal)
					|| proposal.isEmpty())
			{
				proposals.add(createProposal(field, offset));
			}
		}
		// Operations
		for (INode op : getOperations(type))
		{
			if (AstNameUtil.getName(op).startsWith(proposal)
					|| proposal.isEmpty())
			{
				proposals.add(createProposal(op, offset));
			}
		}
		// Functions
		for (INode fn : getFunctions(type))
		{
			if (AstNameUtil.getName(fn).startsWith(proposal)
					|| proposal.isEmpty())
			{
				proposals.add(createProposal(fn, offset));
			}
		}
		// Types
		for (INode tp : getTypes(type))
		{
			if (AstNameUtil.getName(tp).startsWith(proposal)
					|| proposal.isEmpty())
			{
				proposals.add(createProposal(tp, offset));
			}
		}
	}

	private ICompletionProposal createProposal(INode node, int offset)
	{
		String name = AstNameUtil.getName(node);
		if (node instanceof ATypeDefinition)
		{
			name = ((ATypeDefinition) node).getLocation().getModule() + "`"
					+ name;
		}
		IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$
		return new CompletionProposal(name, offset, 0, name.length(), imgProvider.getImageLabel(node, 0), name, info, name);
	}

	private ICompletionProposal createProposal(INode node, int offset,
			VdmCompletionContext info)
	{
		String name = AstNameUtil.getName(node);
		if (node instanceof ATypeDefinition)
		{
			name = ((ATypeDefinition) node).getLocation().getModule() + "`"
					+ name;
		}
		IContextInformation info2 = new ContextInformation(name, name); //$NON-NLS-1$
		return new CompletionProposal(name, offset
				- info.proposalPrefix.length(), info.proposalPrefix.length(), name.length(), imgProvider.getImageLabel(node, 0), name, info2, node.toString());
	}

	private INode getType(String typeName, List<INode> ast)
	{
		for (INode node : ast)
		{
			if (AstNameUtil.getName(node).equals(typeName))
			{
				return node;
			}
		}
		return null;
	}

	private List<INode> getFields(INode node)
	{
		List<INode> fields = new Vector<INode>();
		List<PDefinition> list = getDefinitions(node);

		if (list != null)
		{
			for (PDefinition definition : list)
			{
				if (definition instanceof ALocalDefinition
						|| definition instanceof AValueDefinition
						|| definition instanceof AInstanceVariableDefinition)
				{
					fields.add(definition);
				}
			}
		}
		return fields;
	}

	private List<INode> getTypes(INode node)
	{
		List<INode> types = new Vector<INode>();
		List<PDefinition> list = getDefinitions(node);
		if (list != null)
		{
			for (PDefinition definition : list)
			{
				if (definition instanceof ATypeDefinition)
				{
					types.add(definition);
				}
			}
		}
		return types;
	}

	private List<INode> getOperations(INode node)
	{
		List<INode> ops = new Vector<INode>();
		List<PDefinition> list = getDefinitions(node);

		if (list != null)
		{
			for (PDefinition definition : list)
			{
				if (definition instanceof AExplicitOperationDefinition
						|| definition instanceof AImplicitOperationDefinition)
				{
					ops.add(definition);
				}
			}
		}
		return ops;
	}

	private List<INode> getFunctions(INode node)
	{
		List<INode> fns = new Vector<INode>();
		List<PDefinition> list = getDefinitions(node);

		if (list != null)
		{
			for (PDefinition definition : list)
			{
				if (definition instanceof AExplicitFunctionDefinition
						|| definition instanceof AImplicitFunctionDefinition)
				{
					fns.add(definition);
				}
			}
		}
		return fns;
	}

	private List<PDefinition> getDefinitions(INode node)
	{
		List<PDefinition> list = null;
		if (node instanceof SClassDefinition)
		{
			list = ((SClassDefinition) node).getDefinitions();
		} else if (node instanceof AModuleModules)
		{
			list = ((AModuleModules) node).getDefs();
		}
		return list;
	}

	private List<INode> getAst(VdmDocument document)
	{
		List<INode> ast = new Vector<INode>();
		ast.addAll(document.getProject().getModel().getRootElementList());
		ast.addAll(document.getSourceUnit().getParseList());// maybe add broken parse tree
		return ast;
	}

	private List<INode> getLocalFileAst(VdmDocument document)
	{
		List<INode> ast = new Vector<INode>();
		ast.addAll(document.getSourceUnit().getParseList());
		return ast;
	}
}