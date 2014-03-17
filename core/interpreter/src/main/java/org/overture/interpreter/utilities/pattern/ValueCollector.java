package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.pattern.ASetMultipleBindAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ATypeMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ValueCollector extends QuestionAnswerAdaptor<ObjectContext, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public ValueCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ValueList caseASetMultipleBind(ASetMultipleBind node,
			ObjectContext ctxt) throws AnalysisException
	{
		return ASetMultipleBindAssistantInterpreter.getValues(node, ctxt);
	}
	
	@Override
	public ValueList caseATypeMultipleBind(ATypeMultipleBind node,
			ObjectContext ctxt) throws AnalysisException
	{
		return ATypeMultipleBindAssistantInterpreter.getValues(node, ctxt);
	}
	
	@Override
	public ValueList defaultPMultipleBind(PMultipleBind node,
			ObjectContext ctxt) throws AnalysisException
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
