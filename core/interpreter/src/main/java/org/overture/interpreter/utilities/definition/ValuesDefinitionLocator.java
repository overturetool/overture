package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.AAssignmentDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AEqualsDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AInstanceVariableDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AValueDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

/***************************************
 * 
 * This method locates a value for a definition node. 
 * 
 * @author gkanos
 *
 ****************************************/
public class ValuesDefinitionLocator extends QuestionAnswerAdaptor<ObjectContext, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public ValuesDefinitionLocator(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ValueList caseAAssignmentDefinition(AAssignmentDefinition def,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AAssignmentDefinitionAssistantInterpreter.getValues(def, ctxt);
		return PExpAssistantInterpreter.getValues(def.getExpression(), ctxt);
	}
	
	@Override
	public ValueList caseAEqualsDefinition(AEqualsDefinition def,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AEqualsDefinitionAssistantInterpreter.getValues(def, ctxt);
		ValueList list = PExpAssistantInterpreter.getValues(def.getTest(), ctxt);

		if (def.getSetbind() != null)
		{
			list.addAll(ASetBindAssistantInterpreter.getValues(def.getSetbind(), ctxt));
		}

		return list;
	}
	
	@Override
	public ValueList caseAInstanceVariableDefinition(
			AInstanceVariableDefinition def, ObjectContext ctxt)
			throws AnalysisException
	{
		//return AInstanceVariableDefinitionAssistantInterpreter.getValues(def, ctxt);
		return PExpAssistantInterpreter.getValues(def.getExpression(), ctxt);
	}
	
	@Override
	public ValueList caseAValueDefinition(AValueDefinition def,
			ObjectContext ctxt) throws AnalysisException
	{
		//return AValueDefinitionAssistantInterpreter.getValues(def, ctxt);
		return PExpAssistantInterpreter.getValues(def.getExpression(), ctxt);
	}
	
	@Override
	public ValueList defaultPDefinition(PDefinition def, ObjectContext ctxt)
			throws AnalysisException
	{
		return new ValueList();
	}

	@Override
	public ValueList createNewReturnValue(INode node, ObjectContext question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueList createNewReturnValue(Object node, ObjectContext question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
