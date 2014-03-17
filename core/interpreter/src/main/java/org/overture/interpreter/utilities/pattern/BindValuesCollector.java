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
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.ValueList;

public class BindValuesCollector extends QuestionAnswerAdaptor<Context, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public BindValuesCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ValueList caseASetMultipleBind(ASetMultipleBind node,
			Context ctxt) throws AnalysisException
	{
		return ASetMultipleBindAssistantInterpreter.getBindValues(node, ctxt);
	}
	
	@Override
	public ValueList caseATypeMultipleBind(ATypeMultipleBind node,
			Context ctxt) throws AnalysisException
	{
		return ATypeMultipleBindAssistantInterpreter.getBindValues(node, ctxt);
	}
	
	@Override
	public ValueList defaultPMultipleBind(PMultipleBind node, Context question)
			throws AnalysisException
	{
		return new ValueList();
	}

	@Override
	public ValueList createNewReturnValue(INode node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueList createNewReturnValue(Object node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
	

}
