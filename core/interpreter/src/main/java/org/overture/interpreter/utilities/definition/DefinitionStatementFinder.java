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
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;

/***************************************
 * 
 * This method finds a statement within a definition. 
 * 
 * @author gkanos
 *
 ****************************************/

public class DefinitionStatementFinder extends QuestionAnswerAdaptor<Integer, PStm>
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
		//I don't think this needs to be unfolded.
		return SClassDefinitionAssistantInterpreter.findStatement(def, lineno);
		
	}
	
	@Override
	public PStm caseAExplicitOperationDefinition(
			AExplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		//return AExplicitOperationDefinitionAssistantInterpreter.findStatement(def, lineno);
		return PStmAssistantInterpreter.findStatement(def.getBody(), lineno);
	}
	
	@Override
	public PStm caseAImplicitOperationDefinition(
			AImplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		//return AImplicitOperationDefinitionAssistantInterpreter.findStatement(def, lineno);
		return def.getBody() == null ? null
				: PStmAssistantInterpreter.findStatement(def.getBody(), lineno);
	}
	
	@Override
	public PStm caseAThreadDefinition(AThreadDefinition def, Integer lineno)
			throws AnalysisException
	{
		//return AThreadDefinitionAssistantInterpreter.findStatement(def, lineno);
		return PStmAssistantInterpreter.findStatement(def.getStatement(), lineno);
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
