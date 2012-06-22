package org.overture.interpreter.eval;

import java.lang.reflect.Method;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.messages.InternalException;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Settings;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.state.AModuleModulesRuntime;
import org.overture.interpreter.runtime.state.SClassDefinitionRuntime;
import org.overture.interpreter.values.NaturalOneValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;

public class DelegateExpressionEvaluator extends ExpressionEvaluator
{
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 4591747505913606307L;

	@Override
	public Value caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			Context ctxt) throws AnalysisException
	{
		LexLocation location = node.getLocation();
		BreakpointManager.getBreakpoint(node).check(node.getLocation(), ctxt);

		if (location.module.equals("VDMUtil") ||
			location.module.equals("DEFAULT"))
		{
    		if (ctxt.title.equals("get_file_pos()"))
    		{
    			// This needs location information from the context, so we
    			// can't just call down to a native method for this one.

    			return get_file_pos(ctxt);
    		}
		}

		if (location.module.equals("IO") ||
			location.module.equals("DEFAULT"))
		{
			if (ctxt.title.equals("freadval(filename)"))
			{
				// This needs type invariant information from the context, so we
				// can't just call down to a native method for this one.

				try
				{
					LexNameToken arg = new LexNameToken("IO", "filename", location);
					Value fval = ctxt.get(arg);
					
					// We can't link with the IO class directly because it's in the default
					// package, so we reflect our way over to it.
					
					@SuppressWarnings("rawtypes")
					Class io = Class.forName("IO");
					@SuppressWarnings("unchecked")
					Method m = io.getMethod("freadval", new Class[] {Value.class, Context.class});
					return (Value)m.invoke(io.newInstance(), new Object[] {fval, ctxt});
				}
				catch (Exception e)
				{
					throw new InternalException(62, "Cannot invoke native method: " + e.getMessage());
				}
			}
		}

		
		if (Settings.dialect == Dialect.VDM_SL)
		{
			ModuleInterpreter i = (ModuleInterpreter)Interpreter.getInstance();
			AModuleModules module = i.findModule(location.module);

			if (module != null)
			{
				AModuleModulesRuntime	state =VdmRuntime.getNodeState(module);
				if (state.hasDelegate())
				{
					return state.invokeDelegate(ctxt);
				}
			}
		}
		else
		{
    		ObjectValue self = ctxt.getSelf();

    		if (self == null)
    		{
    			ClassInterpreter i = (ClassInterpreter)Interpreter.getInstance();
    			SClassDefinition cls = i.findClass(location.module);

    			if (cls != null)
    			{
    				SClassDefinitionRuntime state =VdmRuntime.getNodeState(cls);
    				if (state.hasDelegate())
    				{
    					return state.invokeDelegate(ctxt);
    				}
    			}
    		}
    		else
    		{
    			if (self.hasDelegate())
    			{
    				return self.invokeDelegate(ctxt);
    			}
    		}
		}

		return RuntimeError.abort(node.getLocation(),4024, "'not yet specified' expression reached", ctxt);
	}
	
	
	private Value get_file_pos(Context ctxt)
	{
		try
		{
			ValueList tuple = new ValueList();
			Context outer = ctxt.getRoot().outer;
			RootContext root = outer.getRoot();

			tuple.add(new SeqValue(ctxt.location.file.getPath()));
			tuple.add(new NaturalOneValue(ctxt.location.startLine));
			tuple.add(new NaturalOneValue(ctxt.location.startPos));
			tuple.add(new SeqValue(ctxt.location.module));

			int bra = root.title.indexOf('(');

			if (bra > 0)
			{
    			tuple.add(new SeqValue(root.title.substring(0, bra)));
			}
			else
			{
				tuple.add(new SeqValue(""));
			}

			return new TupleValue(tuple);
		}
		catch (ValueException e)
		{
			throw new ContextException(e, ctxt.location);
		}
		catch (Exception e)
		{
			throw new ContextException(4076, e.getMessage(), ctxt.location, ctxt);
		}
	}
}
