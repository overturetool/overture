package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.eval.BindState;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;

/***************************************
 * This class implements a way to collect the values that are single binded.
 * 
 * @author gkanos
 ****************************************/

public class SingleBindValuesCollector extends
		QuestionAnswerAdaptor<BindState, ValueList>
{
	protected IInterpreterAssistantFactory af;

	public SingleBindValuesCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public ValueList caseASetBind(ASetBind bind, BindState state)
			throws AnalysisException
	{
		try
		{
			ValueList results = new ValueList();
			ValueSet elements = bind.getSet().apply(VdmRuntime.getExpressionEvaluator(), state.ctxt).setValue(state.ctxt);
			elements.sort();

			for (Value e : elements)
			{
				e = e.deref();

				if (e instanceof SetValue && state.permuted)
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
	public ValueList caseATypeBind(ATypeBind bind, BindState state)
			throws AnalysisException
	{
		return af.createPTypeAssistant().getAllValues(bind.getType(), state.ctxt);
	}

	@Override
	public ValueList defaultPBind(PBind bind, BindState state)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public ValueList createNewReturnValue(INode node, BindState question)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public ValueList createNewReturnValue(Object node, BindState question)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}
}
