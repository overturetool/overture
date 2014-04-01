package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.AAssignmentDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AClassInvariantDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AEqualsDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AExplicitFunctionDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AExplicitOperationDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AImplicitFunctionDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AImplicitOperationDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AInstanceVariableDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.APerSyncDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AStateDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AThreadDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.ATypeDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AValueDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;


/***************************************
 * 
 * This method finds a expession within a definition. 
 * 
 * @author gkanos
 *
 ****************************************/
public class ExpressionFinder extends QuestionAnswerAdaptor<Integer, PExp>
{
	protected IInterpreterAssistantFactory af;
	
	public ExpressionFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PExp caseAAssignmentDefinition(AAssignmentDefinition def,
			Integer lineno) throws AnalysisException
	{
		return AAssignmentDefinitionAssistantInterpreter.findExpression((AAssignmentDefinition) def, lineno);
	}
	
	@Override
	public PExp caseAClassInvariantDefinition(AClassInvariantDefinition def,
			Integer lineno) throws AnalysisException
	{
		return AClassInvariantDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp defaultSClassDefinition(SClassDefinition def, Integer lineno)
			throws AnalysisException
	{
		return SClassDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAEqualsDefinition(AEqualsDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AEqualsDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AExplicitFunctionDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAExplicitOperationDefinition(
			AExplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AExplicitOperationDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AImplicitFunctionDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAImplicitOperationDefinition(
			AImplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AImplicitOperationDefinitionAssistantInterpreter.findExpression(def, lineno);
	}

	@Override
	public PExp caseAInstanceVariableDefinition(
			AInstanceVariableDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AInstanceVariableDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAPerSyncDefinition(APerSyncDefinition def, Integer lineno)
			throws AnalysisException
	{
		return APerSyncDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAStateDefinition(AStateDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AStateDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseAThreadDefinition(AThreadDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AThreadDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp caseATypeDefinition(ATypeDefinition def, Integer lineno)
			throws AnalysisException
	{
		return ATypeDefinitionAssistantInterpreter.findExpression(def, lineno);
	}

	@Override
	public PExp caseAValueDefinition(AValueDefinition def, Integer lineno)
			throws AnalysisException
	{
		return AValueDefinitionAssistantInterpreter.findExpression(def, lineno);
	}
	
	@Override
	public PExp defaultPDefinition(PDefinition node, Integer question)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public PExp createNewReturnValue(INode node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PExp createNewReturnValue(Object node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
