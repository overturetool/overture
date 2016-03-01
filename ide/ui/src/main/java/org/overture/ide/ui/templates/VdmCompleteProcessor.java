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

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2.TextReference;
import org.overture.ide.ui.utility.ast.AstNameUtil;

public class VdmCompleteProcessor
{
	private VdmElementImageProvider imgProvider = new VdmElementImageProvider();

	public void computeCompletionProposals(VdmCompletionContext info,
			VdmDocument document, List<ICompletionProposal> proposals,
			int offset)
	{
		List<ICompletionProposal> calculatedProposals = new Vector<ICompletionProposal>();

		switch (info.getType())
		{
			case CallParam:
				break;
			case Dot:
				break;
			case Mk:
				completeMK(info, document, calculatedProposals,offset);
				break;
			case New:
				completeNew(info, document, calculatedProposals, offset);
				break;
			case Quote:
				completeQuotes(info, document, calculatedProposals, offset);
				break;
			case Types:
				completeTypes(info, document, calculatedProposals, offset);
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
				if (replacementDisplayString.contains(cp.getDisplayString()))
				{
					continue;
				}

				replacementDisplayString.add(cp.getDisplayString());
			}
			proposals.add(proposal);

		}
	}

	private void completeMK(VdmCompletionContext info, VdmDocument document,
			List<ICompletionProposal> calculatedProposals, int offset) {

		for(INode def : getAst(document))
		{
			completeRecords(offset, calculatedProposals, info, def);
			completeMk_tokens(offset,calculatedProposals,info, def);
			completetuples(offset, calculatedProposals, info,def);
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
							if (info.getProposalPrefix().isEmpty()
									|| name.toLowerCase().startsWith(info.getProposalPrefix().toLowerCase()))
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
										+ info.getReplacementOffset(), info.getProposalPrefix().length(), replacementString.length(), imgProvider.getImageLabel(node, 0), replacementString, infoComplete, node.toString()));
							}
						}
					}
				});
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void completeRecords(final int offset,
			final List<ICompletionProposal> calculatedProposals,
			VdmCompletionContext info, final INode def) {

		try {
			def.apply(new DepthFirstAnalysisAdaptor() {
				@Override
				public void caseARecordInvariantType(ARecordInvariantType arg0)
						throws AnalysisException {
					
					String name = arg0.getName().getName();
					IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$
					
					
					
					
					String replacementString = name + "(";
					String displayString = replacementString;
					
					String sep = "";

					for (Iterator<AFieldField> iterator = arg0.getFields().iterator(); iterator.hasNext();)
					{
						AFieldField field = iterator.next();

						replacementString += sep + field.getTagname().getName();
						displayString += sep + field.toString();
						sep = ", ";

					}
					
					replacementString += ")";
					displayString += ")";
					
					calculatedProposals.add(new CompletionProposal(replacementString , offset, 0, replacementString.length(), imgProvider.getImageLabel(def, 0), replacementString, info, displayString));
				}
			});
		} catch (AnalysisException e) {
			VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
					+ "faild during record search", e);
		}
		
	}
	
	private void completetuples(final int offset,
			final List<ICompletionProposal> calculatedProposals,
			final VdmCompletionContext info, final INode def) {
		try
		{
			def.apply(new DepthFirstAnalysisAdaptor() {			
				@Override
				public void caseAProductType(AProductType arg0)
						throws AnalysisException {
					
					String name = arg0.toString();
					IContextInformation info = new ContextInformation(name, name);
					
					String replacementString = "(";
					//String display = replacementString;
					
					String sep = "";
					
					for(Iterator<PDefinition> iterator = arg0.getDefinitions().iterator(); iterator.hasNext();)
					{
						PDefinition type = iterator.next();
						replacementString += sep + type.getType();
						//display += sep + type.toString();
						sep = ", ";
					}
					replacementString += ")";
					//display += ")";
					
					calculatedProposals.add(new CompletionProposal(replacementString , offset, 0, replacementString.length(), imgProvider.getImageLabel(def, 0), replacementString, info, replacementString));
				}
			});
			
		}
		catch(AnalysisException e)
		{
			VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
					+ "faild during tuple search", e);
		}
		//
		
		
	}
	
	private void completeMk_tokens(final int offset,
			final List<ICompletionProposal> calculatedProposals,
			final VdmCompletionContext info, INode def) {
		
		String name = "token()";
		String display = "mk_token() Token Representation, can take an arbitary expression";
		IContextInformation ctxtInfo = new ContextInformation(name, name); //$NON-NLS-1$
		calculatedProposals.add(new CompletionProposal(name, offset, 0, name.length() - 1, imgProvider.getImageLabel(def, 0), name, ctxtInfo, display));
		
	}

	public void completeTypes(VdmCompletionContext info, VdmDocument document,
			List<ICompletionProposal> proposals, int offset)
	{
		for (INode element : getAst(document))
		{
			if (info.getType() == SearchType.Types)
			{
				String name = AstNameUtil.getName(element);
				if (name.startsWith(info.getProposalPrefix())
						|| name.length() == 0)
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

					if (name.toLowerCase().startsWith(info2.getProposalPrefix().toLowerCase()))
					{
						proposals.add(new CompletionProposal(name, offset
								- info2.getProposalPrefix().length(), info2.getProposalPrefix().length(), name.length(), imgProvider.getImageLabel(element, 0), name, info, element.toString()));
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

					IContextInformation info = new ContextInformation(name, name); //$NON-NLS-1$

					int curOffset = offset + info2.getReplacementOffset();// - info2.proposalPrefix.length();
					int length = name.length();
					int replacementLength = info2.getProposalPrefix().length();

					if (info2.getProposalPrefix().equals("<" + baseValue + ">"))
					{
						curOffset = offset;
						replacementLength = 0;
					}

					if (("<" + baseValue).toLowerCase().startsWith(info2.getProposalPrefix().toLowerCase()))
					{
						proposals.add(new CompletionProposal(name, curOffset, replacementLength, length, imgProvider.getImageLabel(node, 0), name, info, name));
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
						if (field.getTag().toLowerCase().startsWith(info.getProposalPrefix().toLowerCase()))
						{
							proposals.add(createProposal(field, offset, info));
						}
					}
				}
			}
		} catch (Exception e)
		{
			VdmUIPlugin.log("Completion error in " + getClass().getSimpleName(), e);
		}
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
				- info.getProposalPrefix().length(), info.getProposalPrefix().length(), name.length(), imgProvider.getImageLabel(node, 0), name, info2, node.toString());
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
