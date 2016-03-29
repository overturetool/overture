package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateContext;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;

public final class VdmCompletionExtractor {
	
	private static VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	private static VdmCompletionHelper VdmHelper = new VdmCompletionHelper();
	private static VdmFunctionCompletionExtractor VdmFunctionHelper = new VdmFunctionCompletionExtractor();
	private static VdmOperationCompletionExtractor VdmOperationHelper = new VdmOperationCompletionExtractor();

	private ArrayList<String> basicTypes = new ArrayList<String>();
	private ArrayList<String> dynamicTemplateProposals = new ArrayList<String>();
	private ArrayList<String> dynamicProposals = new ArrayList<String>();
	
	public VdmCompletionExtractor(){
		basicTypes.add("nat");
		basicTypes.add("nat1");
		basicTypes.add("bool");
		basicTypes.add("int");
		basicTypes.add("real");
		basicTypes.add("char");
	}
	
	public void generateCompleteProposals(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			final int offset, List<INode> Ast, final TemplateContext context,final ITextViewer viewer)
	{
		for (final INode element : Ast)
		{
			try
			{
				element.apply(new DepthFirstAnalysisAdaptor()
				{
					
					@Override
					public void caseAInstanceVariableDefinition(AInstanceVariableDefinition node)
                            throws AnalysisException{
						String name = node.toString();
						
						if(!VdmHelper.checkForDuplicates(name,dynamicTemplateProposals)){
							VdmHelper.createProposal(node,name,name,"Instance Variable",info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}

					@Override
					public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = VdmFunctionHelper.explicitFunctionNameExtractor(node);
						
						if(VdmHelper.nullOrEmptyCheck(extractedName[1]) && !VdmHelper.checkForDuplicates(extractedName[1],dynamicTemplateProposals)){
							VdmHelper.dynamicTemplateCreator(extractedName,"Explicit Function",offset,context,proposals,info,viewer,node.getLocation().getEndOffset());
							dynamicTemplateProposals.add(extractedName[1]);
						}
					}
					
					@Override
					public void caseAImplicitFunctionDefinition(AImplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = VdmFunctionHelper.implicitFunctionNameExtractor(node);
						
						if(VdmHelper.nullOrEmptyCheck(extractedName[1]) && !VdmHelper.checkForDuplicates(extractedName[1],dynamicTemplateProposals)){
							VdmHelper.dynamicTemplateCreator(extractedName,"Implicit Function",offset,context,proposals,info,viewer,node.getLocation().getEndOffset());
							dynamicTemplateProposals.add(extractedName[1]);
						}	
					}
					
					@Override
					public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node)
                            throws AnalysisException{
						String extractedName[] = VdmOperationHelper.explicitOperationNameExtractor(node);
						
						if(VdmHelper.nullOrEmptyCheck(extractedName[1]) && !VdmHelper.checkForDuplicates(extractedName[1],dynamicTemplateProposals)){
							VdmHelper.dynamicTemplateCreator(extractedName,"Explicit Operation",offset,context,proposals,info,viewer,node.getLocation().getEndOffset());
							dynamicTemplateProposals.add(extractedName[1]);
						}	
					}
					
					@Override
					public void caseAImplicitOperationDefinition(AImplicitOperationDefinition node)
                            throws AnalysisException{
						String extractedName[] = VdmOperationHelper.implicitOperationNameExtractor(node);
						
						if(VdmHelper.nullOrEmptyCheck(extractedName[1]) && !VdmHelper.checkForDuplicates(extractedName[1],dynamicTemplateProposals)){
							VdmHelper.dynamicTemplateCreator(extractedName,"Implicit Operation",offset,context,proposals,info,viewer,node.getLocation().getEndOffset());
							dynamicTemplateProposals.add(extractedName[1]);
						}	
					}
					
					@Override
					public void caseAVariableExp(AVariableExp node)
			                  throws AnalysisException{
						String name = node.toString();
						
						if(!VdmHelper.checkForDuplicates(name,dynamicTemplateProposals)){
							VdmHelper.createProposal(node,name,name,"Variable Exp",info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}

				});
			} catch (AnalysisException e)
			{
				VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
						+ "faild during populateNameList", e);
			}
		}
		basicTypeProposalFunction(info, proposals, offset);
		dynamicTemplateProposals.clear();
		dynamicProposals.clear();
	}
	
	private void basicTypeProposalFunction(final VdmCompletionContext info, final List<ICompletionProposal> proposals,
			final int offset){
		
		Iterator<String> iter = basicTypes.iterator();
		while(iter.hasNext()){
			String item = iter.next();
			VdmHelper.createProposal(null,item,item,"Basic Type",info,proposals,offset);			
		}
	}
}