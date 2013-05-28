package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.PStateDesignatorCG;

public class StateDesignatorVisitor extends QuestionAnswerAdaptor<CodeGenInfo, PStateDesignatorCG>
{

	private static final long serialVersionUID = 7252896277917207085L;

	public StateDesignatorVisitor()
	{
		
	}
	
	@Override
	public PStateDesignatorCG caseAFieldStateDesignator(
			AFieldStateDesignator node, CodeGenInfo question)
			throws AnalysisException
	{
		PStateDesignatorCG stateDesignator = node.getObject().apply(question.getStateDesignatorVisitor(), question);
		String fieldName = node.getField().getName();
		
		AFieldStateDesignatorCG field = new AFieldStateDesignatorCG();
		field.setObject(stateDesignator);
		field.setField(fieldName);
		
		return field;
	}

	@Override
	public PStateDesignatorCG caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, CodeGenInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();
		
		AIdentifierStateDesignatorCG identifier = new AIdentifierStateDesignatorCG();
		identifier.setName(name);
		
		return identifier;
	}
		
}
