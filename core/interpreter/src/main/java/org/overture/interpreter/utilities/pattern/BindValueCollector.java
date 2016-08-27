package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

/***************************************
 * This class implements a way to collect values from a bind.
 * 
 * @author gkanos
 ****************************************/
public class BindValueCollector extends
		QuestionAnswerAdaptor<ObjectContext, ValueList>
{
	protected IInterpreterAssistantFactory af;

	public BindValueCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public ValueList caseASetBind(ASetBind bind, ObjectContext ctxt)
			throws AnalysisException
	{
		return af.createPExpAssistant().getValues(bind.getSet(), ctxt);
	}

	@Override
	public ValueList caseASeqBind(ASeqBind bind, ObjectContext ctxt)
			throws AnalysisException
	{
		return af.createPExpAssistant().getValues(bind.getSeq(), ctxt);
	}

	@Override
	public ValueList caseATypeBind(ATypeBind bind, ObjectContext ctxt)
			throws AnalysisException
	{
		return new ValueList();
	}

	@Override
	public ValueList defaultPBind(PBind bind, ObjectContext ctxt)
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
