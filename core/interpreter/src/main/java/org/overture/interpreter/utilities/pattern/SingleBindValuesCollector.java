package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;

/***************************************
* 
* This class implements a way to collect the values that are single binded.
* 
* @author gkanos
*
****************************************/

public class SingleBindValuesCollector extends QuestionAnswerAdaptor<Context, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public SingleBindValuesCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ValueList caseASetBind(ASetBind bind, Context ctxt)
			throws AnalysisException
	{
		//return ASetBindAssistantInterpreter.getBindValues(bind, ctxt);
		try
		{
			ValueList results = new ValueList();
			ValueSet elements = bind.getSet().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			elements.sort();

			for (Value e : elements)
			{
				e = e.deref();

				if (e instanceof SetValue)
				{
					SetValue sv = (SetValue) e;
					results.addAll(sv.permutedSets());
				} else
				{
					results.add(e);
				}
			}

			return results;
		} catch (AnalysisException e)
		{
			if (e instanceof ValueException)
			{
				VdmRuntimeError.abort(bind.getLocation(), (ValueException) e);

			}
			return null;
		}
	}
	
	@Override
	public ValueList caseATypeBind(ATypeBind bind, Context ctxt)
			throws AnalysisException
	{
		//return ATypeBindAssistantInterpreter.getBindValues(bind, ctxt);
		return af.createPTypeAssistant().getAllValues(bind.getType(), ctxt);
	}

	@Override
	public ValueList defaultPBind(PBind bind, Context ctxt)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public ValueList createNewReturnValue(INode node, Context question)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public ValueList createNewReturnValue(Object node, Context question)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}
}
