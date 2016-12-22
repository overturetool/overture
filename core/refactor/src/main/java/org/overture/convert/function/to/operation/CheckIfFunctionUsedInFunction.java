package org.overture.convert.function.to.operation;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.ACallStm;

public class CheckIfFunctionUsedInFunction  extends DepthFirstAnalysisAdaptor {

	private AExplicitFunctionDefinition function;
	private boolean inFunction = false;
	private boolean inFunctionHit = false;
	
	public CheckIfFunctionUsedInFunction(AExplicitFunctionDefinition function){
		this.function = function;
	}
	
	@Override
	public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node) throws AnalysisException {
		inFunction = true;
		if(!inFunctionHit){
			node.getBody().apply(this);
		}
	}
	
	@Override
	public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node) throws AnalysisException {
		inFunction = false;
	}
	
	@Override
	public void caseAApplyExp(AApplyExp node) throws AnalysisException {

		if (compareNodeLocation(node.getType().getLocation()) && inFunction){
			inFunctionHit = true;
		}
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException
	{
		if (compareNodeLocation(node.getLocation()) && inFunction)
		{
			inFunctionHit = true;
		}
	}
	
	private boolean compareNodeLocation(ILexLocation node){
		if(node.getStartLine() == function.getLocation().getStartLine()){
			return true;
		}
		return false;
	}
	
	public boolean isFunctionUsedInFunction(){
		return inFunctionHit;
	}
}
