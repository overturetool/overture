package org.overture.interpreter.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.FieldMap;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.Value;

public class AFieldExpAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFieldExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public Value evaluate(AFieldExp node, Context ctxt)
			throws AnalysisException
	{
		Value v = node.getObject().apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		PType objtype;
		Value r;

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
			VdmRuntimeError.abort(node.getLocation(), 4006, "Type " + objtype
					+ " has no field " + node.getField().getName(), ctxt);
		}

		return r;
	}

}
