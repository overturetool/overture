package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AApplyExpressionTraceCoreDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AApplyExpressionTraceCoreDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static TraceNode expand(AApplyExpressionTraceCoreDefinition core,
//			Context ctxt)
//	{
//		List<PExp> newargs = new Vector<PExp>();
//		List<PExp> args = null;
//
//		if (core.getCallStatement() instanceof ACallStm)
//		{
//			ACallStm stmt = (ACallStm) core.getCallStatement();
//			args = stmt.getArgs();
//		} else
//		{
//			ACallObjectStm stmt = (ACallObjectStm) core.getCallStatement();
//			args = stmt.getArgs();
//		}
//
//		for (PExp arg : args)
//		{
//			Value v = null;
//			try
//			{
//				v = arg.apply(VdmRuntime.getExpressionEvaluator(), ctxt).deref();
//			} catch (AnalysisException e1)
//			{
//				e1.printStackTrace();
//			}
//
//			if (v instanceof ObjectValue)
//			{
//				newargs.add(arg.clone());
//			} else
//			{
//				// TODO This rewrites the source code and enables stepping when evaluating the
//				// arguments where the location is off since the new arguments do not exist in the source
//				// file. What to do? Use the same location as the call statement? or..
//				String value = v.toString();
//				LexTokenReader ltr = new LexTokenReader(value, Settings.dialect, arg.getLocation());
//				ExpressionReader er = new ExpressionReader(ltr);
//				er.setCurrentModule(core.getCurrentModule());
//
//				try
//				{
//					newargs.add(er.readExpression());
//				} catch (ParserException e)
//				{
//					newargs.add(arg.clone()); // Give up!
//				} catch (LexException e)
//				{
//					newargs.add(arg.clone()); // Give up!
//				}
//			}
//		}
//
//		PStm newStatement = null;
//
//		if (core.getCallStatement() instanceof ACallStm)
//		{
//			ACallStm stmt = (ACallStm) core.getCallStatement();
//			newStatement = AstFactory.newACallStm(stmt.getName().clone(), newargs);
//		} else
//		{
//			ACallObjectStm stmt = (ACallObjectStm) core.getCallStatement();
//
//			if (stmt.getClassname() != null)
//			{
//				newStatement = AstFactory.newACallObjectStm(stmt.getDesignator().clone(), stmt.getClassname().clone(), newargs);
//			} else
//			{
//				newStatement = AstFactory.newACallObjectStm(stmt.getDesignator().clone(), (LexIdentifierToken) stmt.getFieldname().clone(), newargs);
//			}
//		}
//
//		return new StatementTraceNode(newStatement);
//
//	}

}
