package org.overture.interpreter.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.ASporadicStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.AStopStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
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
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AStartStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public void start(AStartStm node, ObjectValue target, OperationValue op,
			Context ctxt) throws AnalysisException
	{
		if (op.body instanceof APeriodicStm)
		{
			RootContext global = ClassInterpreter.getInstance().initialContext;
			Context pctxt = new ObjectContext(af, op.name.getLocation(), "async", global, target);
			APeriodicStm ps = (APeriodicStm) op.body;

			// We disable the swapping and time (RT) as periodic evaluation should be "free".
			try
			{
				pctxt.threadState.setAtomic(true);
				ps.apply(VdmRuntime.getStatementEvaluator(), pctxt); // Ignore return value
			} finally
			{
				pctxt.threadState.setAtomic(false);
			}

			OperationValue pop = pctxt.lookup(ps.getOpname()).operationValue(pctxt);

			long period = ps.getPeriod();
			long jitter = ps.getJitter();
			long delay = ps.getDelay();
			long offset = ps.getOffset();

			// Note that periodic threads never set the stepping flag

			new PeriodicThread(target, pop, period, jitter, delay, offset, 0, false).start();
		} else if (op.body instanceof ASporadicStm)
		{
			RootContext global = ClassInterpreter.getInstance().initialContext;
			Context pctxt = new ObjectContext(af, op.name.getLocation(), "sporadic", global, target);
			ASporadicStm ss = (ASporadicStm) op.body;

			// We disable the swapping and time (RT) as sporadic evaluation should be "free".
			try
			{
				pctxt.threadState.setAtomic(true);
				ss.apply(VdmRuntime.getStatementEvaluator(), pctxt); // Ignore return value
			} finally
			{
				pctxt.threadState.setAtomic(false);
			}

			OperationValue pop = pctxt.lookup(ss.getOpname()).operationValue(pctxt);

			long delay = ss.getMinDelay();
			long jitter = ss.getMaxDelay(); // Jitter used for maximum delay
			long offset = ss.getOffset();
			long period = 0;

			// Note that periodic threads never set the stepping flag
			new PeriodicThread(target, pop, period, jitter, delay, offset, 0, true).start();
		} else
		{
			new ObjectThread(node.getLocation(), target, ctxt).start();
		}
	}

	public PExp findExpression(AStopStm stm, int lineno)
	{
		return af.createPExpAssistant().findExpression(stm.getObj(), lineno);
	}
}
