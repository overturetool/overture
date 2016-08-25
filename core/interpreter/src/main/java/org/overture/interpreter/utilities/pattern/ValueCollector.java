package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASeqMultipleBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

/***************************************
 * This class implements a way to collect values from binds in a node type
 * 
 * @author gkanos
 ****************************************/
public class ValueCollector extends
		QuestionAnswerAdaptor<ObjectContext, ValueList>
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
		return af.createPExpAssistant().getValues(node.getSet(), ctxt);
	}

	@Override
	public ValueList caseASeqMultipleBind(ASeqMultipleBind node,
			ObjectContext ctxt) throws AnalysisException
	{
		return af.createPExpAssistant().getValues(node.getSeq(), ctxt);
	}

	@Override
	public ValueList caseATypeMultipleBind(ATypeMultipleBind node,
			ObjectContext ctxt) throws AnalysisException
	{
		return new ValueList();
	}

	@Override
	public ValueList defaultPMultipleBind(PMultipleBind node, ObjectContext ctxt)
			throws AnalysisException
	{
		return new ValueList();
	}

	@Override
	public ValueList createNewReturnValue(INode node, ObjectContext question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return new ValueList();
	}

	@Override
	public ValueList createNewReturnValue(Object node, ObjectContext question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return new ValueList();
	}

}
