package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateContext;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.types.PType;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;

public final class VdmNewCompletionExtractor {
	
	private static VdmCompletionHelper VdmHelper = new VdmCompletionHelper();
	private static VdmOperationCompletionExtractor VdmOperationHelper = new VdmOperationCompletionExtractor();

	private ArrayList<String> dynamicTemplateProposals = new ArrayList<String>();

	public void generateNewCompletionProposals(final VdmCompletionContext info,
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
					public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node)
                            throws AnalysisException{
						String extractedName[] = VdmOperationHelper.explicitOperationNameExtractor(node);
						PType classType = node.getClassDefinition().getClasstype();
						if(classType != null){
							String className = classType.toString();
							String operationName = node.getName().toString();
							operationName = operationName.split("\\(")[0];
						
							if(className.equals(operationName) && VdmHelper.nullOrEmptyCheck(extractedName[1])){
								extractedName[0] = "new " + extractedName[0];
								extractedName[1] = "new " + extractedName[1];
								if(!VdmHelper.checkForDuplicates(extractedName[1],dynamicTemplateProposals)){
									VdmHelper.dynamicTemplateCreator(extractedName,"New",offset,context,proposals,info,viewer,node.getLocation().getEndOffset());
									dynamicTemplateProposals.add(extractedName[1]);
								}	
						
						    	//For the default constructor proposals
						    	String extractedNamesDefaultCtor = (String) ("new " + operationName + "()");		    	
						    	if(!VdmHelper.checkForDuplicates(extractedNamesDefaultCtor,dynamicTemplateProposals)){
						    		VdmHelper.createProposal(null,extractedNamesDefaultCtor,extractedNamesDefaultCtor,"Default constructor",info,proposals,offset);
									dynamicTemplateProposals.add(extractedNamesDefaultCtor);
								}
							}
						}	
					}
				});
			} catch (AnalysisException e)
			{
				e.printStackTrace();
				VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
						+ "faild during populateNameList New", e);
			}
		}
	}
}
