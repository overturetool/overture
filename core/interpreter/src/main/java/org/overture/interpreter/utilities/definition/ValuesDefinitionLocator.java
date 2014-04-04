package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.AAssignmentDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AEqualsDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AInstanceVariableDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.AValueDefinitionAssistantInterpreter;
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
		return AAssignmentDefinitionAssistantInterpreter.getValues((AAssignmentDefinition) def, ctxt);
	}
	
	@Override
	public ValueList caseAEqualsDefinition(AEqualsDefinition def,
			ObjectContext ctxt) throws AnalysisException
	{
		return AEqualsDefinitionAssistantInterpreter.getValues((AEqualsDefinition) def, ctxt);
	}
	
//	if (def instanceof AAssignmentDefinition)
//	{
//		
//	} else if (def instanceof AEqualsDefinition)
//	{
//		
//	} else if (def instanceof AInstanceVariableDefinition)
//	{
//		return AInstanceVariableDefinitionAssistantInterpreter.getValues((AInstanceVariableDefinition) def, ctxt);
//	} else if (def instanceof AValueDefinition)
//	{
//		return AValueDefinitionAssistantInterpreter.getValues((AValueDefinition) def, ctxt);
//	} else
//	{
//		return new ValueList();
//	}

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
