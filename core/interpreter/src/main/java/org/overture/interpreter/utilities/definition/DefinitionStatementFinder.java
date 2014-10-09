package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * This method finds a statement within a definition.
 * 
 * @author gkanos
 ****************************************/

public class DefinitionStatementFinder extends
		QuestionAnswerAdaptor<Integer, PStm>
{
	protected IInterpreterAssistantFactory af;

	public DefinitionStatementFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PStm defaultSClassDefinition(SClassDefinition def, Integer lineno)
			throws AnalysisException
	{
		// I don't think this needs to be unfolded. gk
		return af.createSClassDefinitionAssistant().findStatement(def, lineno);

	}

	@Override
	public PStm caseAExplicitOperationDefinition(
			AExplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createPStmAssistant().findStatement(def.getBody(), lineno);
	}

	@Override
	public PStm caseAImplicitOperationDefinition(
			AImplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		return def.getBody() == null ? null
				: af.createPStmAssistant().findStatement(def.getBody(), lineno);
	}

	@Override
	public PStm caseAThreadDefinition(AThreadDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createPStmAssistant().findStatement(def.getStatement(), lineno);
	}

	@Override
	public PStm defaultPDefinition(PDefinition def, Integer lineno)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public PStm createNewReturnValue(INode node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PStm createNewReturnValue(Object node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
