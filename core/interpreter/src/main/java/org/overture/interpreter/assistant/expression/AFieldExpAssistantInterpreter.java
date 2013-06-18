package org.overture.interpreter.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.FieldMap;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AFieldExpAssistantTC;

public class AFieldExpAssistantInterpreter extends AFieldExpAssistantTC
{

	public static Value evaluate(AFieldExp node, Context ctxt) throws AnalysisException
	{
		Value v = node.getObject().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		PType objtype = null;
		Value r = null;

		if (v.isType(ObjectValue.class))
		{
			ObjectValue ov = v.objectValue(ctxt);
			objtype = ov.type;
			r = ov.get(node.getMemberName(), node.getMemberName().getExplicit());
		} else
		{
			RecordValue rv = v.recordValue(ctxt);
			objtype = rv.type;
			FieldMap fields = rv.fieldmap;
			r = fields.get(node.getField().getName());
		}
		if (r == null)
		{
			VdmRuntimeError.abort(node.getLocation(), 4006, "Type "
					+ objtype + " has no field "
					+ node.getField().getName(), ctxt);
		}

		return r;
	}
	
	public static ValueList getValues(AFieldExp exp, ObjectContext ctxt) 
	{
		ValueList values = PExpAssistantInterpreter.getValues(exp.getObject(),ctxt);
		
		try
		{
			// This evaluation should not affect scheduling as we are trying to
			// discover the sync variables to listen to only.
			
			ctxt.threadState.setAtomic(true);
			Value r = evaluate(exp, ctxt);
			ctxt.threadState.setAtomic(false);

			if (r instanceof UpdatableValue)
			{
				values.add(r);
			}
			
			return values;
		}
		catch (ContextException e)
		{
			if (e.number == 4034)
			{
				return values;	// Non existent variable
			}
			else
			{
				throw e;
			}
		}
		catch (ValueException e)
		{
			VdmRuntimeError.abort(exp.getLocation(), e);
			return null;
		} catch (AnalysisException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static PExp findExpression(AFieldExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getObject(),lineno);
	}

}
