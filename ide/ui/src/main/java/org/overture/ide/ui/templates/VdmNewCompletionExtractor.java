package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateContext;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.types.PType;
import org.overture.ide.ui.editor.core.VdmDocument;

public class VdmNewCompletionExtractor {
	
	private static VdmCompletionHelper VdmHelper = new VdmCompletionHelper();
	
	private ArrayList<String> dynamicTemplateProposals = new ArrayList<String>();

	public void generateNewCompletionProposals(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			final int offset, List<INode> Ast, final TemplateContext context,final ITextViewer viewer)
	{
		for (INode element : Ast)
		{
			try
			{
				element.apply(new DepthFirstAnalysisAdaptor()
				{
					@Override
					public void caseANewExp(ANewExp node) throws AnalysisException{
						
						String name = node.getClassName().toString();
						String[] names = {name, (name + "(")};
						LinkedList<PExp> argsList = node.getArgs();
						List<PType> typeList = new ArrayList<PType>();
						for (int i = 0; i < argsList.size(); i++) {
							PType type = argsList.get(i).getType();
							if(type != null){
								typeList.add(type);
							}
						}
						
						String extractedNames[] = newTemplatePatternGenerator(typeList,names);		    	
				    	if(VdmHelper.nullOrEmptyCheck(extractedNames[1]) && !VdmHelper.checkForDuplicates(extractedNames[1],dynamicTemplateProposals)){
							VdmHelper.dynamicTemplateCreator(extractedNames,"New",offset,context,proposals,info,viewer,node.getLocation().getEndOffset(),info.getProposalPrefix());
							dynamicTemplateProposals.add(extractedNames[1]);
						}
				    	
				    	String[] namesForDefaultCtor = {name, (name + "(")};
				    	
				    	//For the default constructor proposals
				    	String extractedNamesDefaultCtor[] = newTemplatePatternGenerator(null,namesForDefaultCtor);		    	
				    	if(VdmHelper.nullOrEmptyCheck(extractedNamesDefaultCtor[1]) && !VdmHelper.checkForDuplicates(extractedNamesDefaultCtor[1],dynamicTemplateProposals)){
							VdmHelper.dynamicTemplateCreator(extractedNamesDefaultCtor,"New",offset,context,proposals,info,viewer,node.getLocation().getEndOffset(),info.getProposalPrefix());
							dynamicTemplateProposals.add(extractedNamesDefaultCtor[1]);
						}
					}
					
				});
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	private void generateParameterFromType(PType paramType, StringBuilder sbName, StringBuilder sbDisplayName){
		String typeStr = paramType.getClass().getTypeName().toString();
		String[] typeStrArr = typeStr.split("\\."); 
		String typeName = typeStrArr[typeStrArr.length-1];
		
		//AQuoteType, ASeq1SeqType, ASetType, AMapMapType
		switch(typeName){
		case "AQuoteType":
			sbName.append("<${quote}>");
			sbDisplayName.append("<quote>");
			break;
		case "ASeq1SeqType":
			sbName.append("\"${string}\"");
			sbDisplayName.append("string");
			break;
		case "ASetType":
			sbName.append("{${set}}");
			sbDisplayName.append("{x,y,z}");
			break;
		case "AMapMapType":
			sbName.append("{${from} |-> ${to}}");
			sbDisplayName.append("{map x to y}");
		default:
			break;			
		}
	}
	
	private String[] newTemplatePatternGenerator(List<PType> parameterTypes,String[] names){
		StringBuilder sbName = new StringBuilder();
		StringBuilder sbDisplayName = new StringBuilder();
		StringBuilder sbDefaultCtorName = new StringBuilder();
		StringBuilder sbDefaultCtorDisplayName = new StringBuilder();
			
		sbName.append(names[1]);
		sbDisplayName.append(names[1]);
		
		if((parameterTypes != null && !parameterTypes.isEmpty())){
			for (int i = 0; i < parameterTypes.size(); i++) {
				if(i != 0){
					sbName.append(", ");
					sbDisplayName.append(", ");
				}
				generateParameterFromType(parameterTypes.get(i), sbName, sbDisplayName);
			}			
		} else {
			//For creating the default constructor proposal
			sbDefaultCtorName.append(names[1]);
			sbDefaultCtorDisplayName.append(names[1]);
			sbDefaultCtorName.append("${}");
			sbDefaultCtorName.append(")");
			sbDefaultCtorDisplayName.append(")");
			
			names[1] = "new " + sbDefaultCtorName.toString();
			names[0] = sbDefaultCtorDisplayName.toString();
			return names;
		}
		
		sbName.append(")");
		sbDisplayName.append(")");
		
		names[1] = "new " + sbName.toString();
		names[0] = sbDisplayName.toString();

    	return names;
	}
	
	
}
