package org.overture.ide.ui.templates;

import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;

public final class VdmCompletionExtractor {
	
	static VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	
	public VdmCompletionExtractor(){}
	
	    
	public void completeBasicTypes(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			final int offset, List<INode> Ast)
	{
		for (final INode element : Ast)
		{
			try
			{
				element.apply(new DepthFirstAnalysisAdaptor()
				{
					@Override
					public void caseANatNumericBasicType(ANatNumericBasicType node)
							throws AnalysisException
					{
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseANatOneNumericBasicType(ANatOneNumericBasicType node)
							throws AnalysisException
					{
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseABooleanBasicType(ABooleanBasicType node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseAIntNumericBasicType(AIntNumericBasicType node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseARationalNumericBasicType(ARationalNumericBasicType node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseARealNumericBasicType(ARealNumericBasicType node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseACharacterPattern(ACharacterPattern node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseACharBasicType(ACharBasicType node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseATokenBasicType(ATokenBasicType node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void defaultSNumericBasicType(SNumericBasicType node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseAClassInvariantStm(AClassInvariantStm node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseAClassInvariantDefinition(AClassInvariantDefinition node){
						populateProposals(node, node.toString());
					}
					
					@Override
					public void caseANamedInvariantType(ANamedInvariantType node){
						populateProposals(node, node.toString());
					}
					
					void populateProposals(INode node, String name)
					{
						if(findInString(info.proposalPrefix,name))
						{
							
							IContextInformation contextInfo = new ContextInformation(name, name); //$NON-NLS-1$

							int curOffset = offset + info.offset;// - info2.proposalPrefix.length();
							int length = name.length();
							int replacementLength = info.proposalPrefix.length();

							proposals.add(new CompletionProposal(name, curOffset, replacementLength, length, imgProvider.getImageLabel(node, 0), name, contextInfo, name));
							
						}
					}
				});
			} catch (AnalysisException e)
			{
				VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
						+ "faild during populateNameList", e);
			}
		}
	}

    public boolean findInString(String text,String word)
	{
	  return word.toLowerCase().startsWith(text.toLowerCase());
	}
}
