package org.overture.convert.function.to.operation;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.PStm;
import org.overture.refactoring.RefactoringLogger;

public class FunctionOccurrenceSwitcher extends DepthFirstAnalysisAdaptor{
	private AExplicitOperationDefinition toOperation;
	private AExplicitFunctionDefinition function;
	private RefactoringLogger<ConversionFromFuncToOp> refactoringLogger;
	
	public FunctionOccurrenceSwitcher(AExplicitOperationDefinition toOperation, AExplicitFunctionDefinition function, RefactoringLogger<ConversionFromFuncToOp> refactoringLogger){
		this.toOperation = toOperation;
		this.function = function;	
		this.refactoringLogger = refactoringLogger;
	}
	
	private boolean compareNodeLocation(ILexLocation node){
		if(node.getStartLine() == function.getLocation().getStartLine()){
			return true;
		}
		return false;
	}
	
	@Override
	public void caseAApplyExp(AApplyExp node) throws AnalysisException {
		AVariableExp root = null;
		
		if(node.getRoot() instanceof AVariableExp){
			root = (AVariableExp) node.getRoot();
		}
		
		if(root == null){
			return;
		}
		if (compareNodeLocation(node.getType().getLocation())){
			
			if(node.parent() instanceof AReturnStm){
				AReturnStm parent = (AReturnStm) node.parent();			
				AVariableExp aVariableExp = AstFactory.newAVariableExp(toOperation.getName());
				aVariableExp.setType(toOperation.getType());
				aVariableExp.setVardef(toOperation);
				AApplyExp aApplyExp = AstFactory.newAApplyExp(aVariableExp);
				parent.setExpression(aApplyExp);
				refactoringLogger.add(new ConversionFromFuncToOp(node.getLocation(), node.toString()));
			}
		}
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException
	{
		if (node.getRootdef() == null)
		{
			return;
		}
		if (compareNodeLocation(node.getLocation()))
		{
			if(node.parent() instanceof ABlockSimpleBlockStm){
				ABlockSimpleBlockStm parent = (ABlockSimpleBlockStm) node.parent();
				ACallStm newCallStm = AstFactory.newACallStm(toOperation.getName(), node.getArgs());
				replaceStatementInList(parent.getStatements(), node.getLocation(), newCallStm);
			}
		}
	}
	
	public void replaceStatementInList(LinkedList<PStm> stmList, ILexLocation loc, ACallStm newStm){
		
		for (int i = 0; i < stmList.size(); i++) {
			PStm item = stmList.get(i);
			if(item.getLocation().getStartLine() == loc.getStartLine() 
					&& item.getLocation().getStartOffset() == loc.getStartOffset()){
				stmList.add(i, newStm);
				refactoringLogger.add(new ConversionFromFuncToOp(loc, newStm.getName().getName()));
				break;
			}
		}
		
	}
	
}