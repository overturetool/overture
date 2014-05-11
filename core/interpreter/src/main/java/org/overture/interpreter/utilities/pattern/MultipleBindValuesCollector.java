package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
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
* This class implements a way to collect the values that are binded.
* 
* @author gkanos
*
****************************************/

public class MultipleBindValuesCollector extends QuestionAnswerAdaptor<Context, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public MultipleBindValuesCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ValueList caseASetMultipleBind(ASetMultipleBind node,
			Context ctxt) throws AnalysisException
	{
		try
		{
			ValueList vl = new ValueList();
			ValueSet vs = node.getSet().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			vs.sort();

			for (Value v : vs)
			{
				v = v.deref();

				if (v instanceof SetValue)
				{
					SetValue sv = (SetValue) v;
					vl.addAll(sv.permutedSets());
				} else
				{
					vl.add(v);
				}
			}

			return vl;
		} catch (AnalysisException e)
		{
			if (e instanceof ValueException)
			{
				VdmRuntimeError.abort(node.getLocation(), (ValueException) e);
			}
			return null;

		}
	}
	
	@Override
	public ValueList caseATypeMultipleBind(ATypeMultipleBind node,
			Context ctxt) throws AnalysisException
	{
		//return ATypeMultipleBindAssistantInterpreter.getBindValues(node, ctxt);
		return PTypeAssistantInterpreter.getAllValues(node.getType(), ctxt);
	}
	
	@Override
	public ValueList defaultPMultipleBind(PMultipleBind node, Context question)
			throws AnalysisException
	{
		return null;
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
