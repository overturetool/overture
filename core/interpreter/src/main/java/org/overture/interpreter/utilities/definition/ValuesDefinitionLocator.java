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
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

/***************************************
 * This method locates a value for a definition node.
 * 
 * @author gkanos
 ****************************************/
public class ValuesDefinitionLocator extends
		QuestionAnswerAdaptor<ObjectContext, ValueList>
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
		return af.createPExpAssistant().getValues(def.getExpression(), ctxt);
	}

	@Override
	public ValueList caseAEqualsDefinition(AEqualsDefinition def,
			ObjectContext ctxt) throws AnalysisException
	{
		ValueList list = af.createPExpAssistant().getValues(def.getTest(), ctxt);

		if (def.getSetbind() != null)
		{
			list.addAll(af.createPBindAssistant().getBindValues(def.getSetbind(), ctxt, false));
		}

		return list;
	}

	@Override
	public ValueList caseAInstanceVariableDefinition(
			AInstanceVariableDefinition def, ObjectContext ctxt)
			throws AnalysisException
	{
		return af.createPExpAssistant().getValues(def.getExpression(), ctxt);
	}

	@Override
	public ValueList caseAValueDefinition(AValueDefinition def,
			ObjectContext ctxt) throws AnalysisException
	{
		return af.createPExpAssistant().getValues(def.getExpression(), ctxt);
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
