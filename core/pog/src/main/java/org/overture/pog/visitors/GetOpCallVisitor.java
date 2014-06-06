package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.SOperationDefinitionBase;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;

public class GetOpCallVisitor extends AnswerAdaptor<SOperationDefinitionBase> {

	@Override
	public SOperationDefinitionBase createNewReturnValue(INode node) throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SOperationDefinitionBase createNewReturnValue(Object node) throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public SOperationDefinitionBase caseAApplyExp(AApplyExp node) throws AnalysisException {
		return node.getRoot().apply(this);
	}

	@Override
	public SOperationDefinitionBase caseAVariableExp(AVariableExp node) throws AnalysisException {
		return node.getVardef().apply(this);
	}

	@Override
	public SOperationDefinitionBase caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException {
		return node;
	}

	@Override
	public SOperationDefinitionBase caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException {
		return node;
	}

}
