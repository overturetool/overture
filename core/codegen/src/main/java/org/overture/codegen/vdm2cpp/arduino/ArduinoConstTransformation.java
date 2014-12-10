package org.overture.codegen.vdm2cpp.arduino;

import java.util.Set;

import javax.xml.crypto.dsig.Transform;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.analysis.intf.IAnalysis;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ArduinoConstTransformation extends DepthFirstAnalysisAdaptor {

	private static final String ARDUINO = "Arduino";

	private TransAssistantCG transAssistant;

	public ArduinoConstTransformation(
			TransAssistantCG transAssistant) {
		this.transAssistant = transAssistant;
	}

	public void setTransAssistant(TransAssistantCG transAssistant) {
		this.transAssistant = transAssistant;
	}

	@Override
	public void caseAExplicitVarExpCG(AExplicitVarExpCG node)
			throws AnalysisException {
		
		STypeCG type = node.getClassType();
		
		if(type instanceof AClassTypeCG)
		{
			AClassTypeCG classType = (AClassTypeCG) type;
			
			if(classType.getName().equals(ARDUINO))
			{
				AIdentifierVarExpCG replacedVarExp = new AIdentifierVarExpCG();
				replacedVarExp.setIsLambda(false);
				replacedVarExp.setOriginal(node.getName());
				replacedVarExp.setSourceNode(node.getSourceNode());
				replacedVarExp.setType(node.getType().clone());
				
				transAssistant.replaceNodeWith(node, replacedVarExp);
			}
		}
		
	}
	
	@Override
	public void caseAPlainCallStmCG(APlainCallStmCG node)
			throws AnalysisException {
		
		node.setClassType(null);
		
		for(SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}
	}
	
}
