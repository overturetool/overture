package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;

public class IsOpCallVisitor extends AnswerAdaptor<Boolean> {

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException {
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException {
		return false;
	}

	@Override
	public Boolean caseAApplyExp(AApplyExp node) throws AnalysisException {
		return node.getRoot().apply(this);
	}

	@Override
	public Boolean caseAVariableExp(AVariableExp node) throws AnalysisException {
		return node.getVardef().apply(this);
	}

	@Override
	public Boolean caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException {
		return true;
	}

	@Override
	public Boolean caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException {
		return true;
	}

}
