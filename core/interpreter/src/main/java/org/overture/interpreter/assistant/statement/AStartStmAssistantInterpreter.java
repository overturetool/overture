package org.overture.interpreter.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.AStartStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.scheduler.ObjectThread;
import org.overture.interpreter.scheduler.PeriodicThread;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;

public class AStartStmAssistantInterpreter
{

	public static void start(AStartStm node, ObjectValue target, OperationValue op, Context ctxt) throws AnalysisException
	{
		if (op.body instanceof APeriodicStm)
		{
    		RootContext global = ClassInterpreter.getInstance().initialContext;
    		Context pctxt = new ObjectContext(op.name.location, "async", global, target);
    		APeriodicStm ps = (APeriodicStm)op.body;
    		
    		// We disable the swapping and time (RT) as periodic evaluation should be "free".
    		pctxt.threadState.setAtomic(true);
    		ps.apply(VdmRuntime.getStatementEvaluator(), pctxt); // Ignore return value
    		pctxt.threadState.setAtomic(false);
    		
			OperationValue pop = pctxt.lookup(ps.getOpname()).operationValue(pctxt);

			long period = ps.getPeriod();
			long jitter = ps.getJitter();
			long delay  = ps.getDelay();
			long offset = ps.getOffset();
			
			// Note that periodic threads never set the stepping flag

			new PeriodicThread(
				target, pop, period, jitter, delay, offset, 0).start();
		}
		else
		{
			new ObjectThread(node.getLocation(), target, ctxt).start();
		}
	}

	public static PExp findExpression(AStartStm stm, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(stm.getObj(),lineno);
	}

}
