package org.overture.interpreter.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.PStm;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.traces.StatementTraceNode;
import org.overture.interpreter.traces.TraceNode;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.Value;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;

public class AApplyExpressionTraceCoreDefinitionAssistantInterpreter
{

	public static TraceNode expand(AApplyExpressionTraceCoreDefinition core,
			Context ctxt)
	{
		List<PExp> newargs = new Vector<PExp>();
		List<PExp> args = null;

		if (core.getCallStatement() instanceof ACallStm)
		{
			ACallStm stmt = (ACallStm)core.getCallStatement();
			args = stmt.getArgs();
		}
		else
		{
			ACallObjectStm stmt = (ACallObjectStm)core.getCallStatement();
			args = stmt.getArgs();
		}

		for (PExp arg: args)
		{
			Value v = null;
			try
			{
				v = arg.apply(VdmRuntime.getExpressionEvaluator(),ctxt).deref();
			} catch (Throwable e1)
			{
				
				e1.printStackTrace();
			}

			if (v instanceof ObjectValue)
			{
				newargs.add(arg);
			}
			else
			{
				//TODO This rewrites the source code and enables stepping when evaluating the
				//arguments where the location is off since the new arguments do not exist in the source
				// file. What to do? Use the same location as the call statement? or..
    			String value = v.toString();
    			LexTokenReader ltr = new LexTokenReader(value, Settings.dialect,arg.getLocation());
    			ExpressionReader er = new ExpressionReader(ltr);
    			er.setCurrentModule(core.getCurrentModule());

    			try
    			{    				    				
    				newargs.add(er.readExpression());
    			}
    			catch (ParserException e)
    			{
    				newargs.add(arg);		// Give up!
    			}
    			catch (LexException e)
    			{
    				newargs.add(arg);		// Give up!
    			}
			}
		}

		PStm newStatement = null;

		if (core.getCallStatement() instanceof ACallStm)
		{
			ACallStm stmt = (ACallStm)core.getCallStatement();
			newStatement = AstFactory.newACallStm(stmt.getName(), newargs);
		}
		else
		{
			ACallObjectStm stmt = (ACallObjectStm)core.getCallStatement();
			
			if (stmt.getClassname() != null)
			{
				newStatement = AstFactory.newACallObjectStm(
					stmt.getDesignator().clone(), stmt.getClassname(), newargs);
			}
			else
			{
				newStatement = AstFactory.newACallObjectStm(
					stmt.getDesignator(), stmt.getFieldname(), newargs);
			}
		}

		return new StatementTraceNode(newStatement);
		
	}

}
