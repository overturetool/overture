
package org.overture.interpreter.eval;

import java.lang.reflect.Method;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.messages.InternalException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Settings;
import org.overture.interpreter.debug.BreakpointManager;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
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

	protected Value evalDelegatedANotYetSpecified(INode node,
			ILexLocation location, int abortNumber, String type,
			boolean failOnUnhandled, Context ctxt) throws AnalysisException
	{
		BreakpointManager.getBreakpoint(node).check(location, ctxt);

		if (location.getModule().equals("VDMUtil")
				|| location.getModule().equals("DEFAULT"))
		{
			if (ctxt.title.equals("get_file_pos()"))
			{
				// This needs location information from the context, so we
				// can't just call down to a native method for this one.

				return get_file_pos(ctxt);
			}
		}

		if (location.getModule().equals("IO")
				|| location.getModule().equals("DEFAULT"))
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
					Method m = io.getMethod("freadval", new Class[] {
							Value.class, Context.class });
					return (Value) m.invoke(io.newInstance(), new Object[] {
							fval, ctxt });
				} catch (Exception e)
				{
					throw new InternalException(62, "Cannot invoke native method: "
							+ e.getMessage());
				}
			}
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			ModuleInterpreter i = (ModuleInterpreter) Interpreter.getInstance();
			AModuleModules module = i.findModule(location.getModule());

			if (module != null)
			{
				AModuleModulesRuntime state = VdmRuntime.getNodeState(module, ctxt.assistantFactory);
				if (state.hasDelegate())
				{
					return state.invokeDelegate(ctxt);
				}
			}
		} else
		{
			ObjectValue self = ctxt.getSelf();

			if (self == null)
			{
				ClassInterpreter i = (ClassInterpreter) Interpreter.getInstance();
				SClassDefinition cls = i.findClass(location.getModule());

				if (cls != null)
				{
					SClassDefinitionRuntime state = VdmRuntime.getNodeState(ctxt.assistantFactory, cls);
					if (state.hasDelegate())
					{
						return state.invokeDelegate(ctxt);
					}
				}
			} else
			{
				if (self.hasDelegate(ctxt))
				{
					return self.invokeDelegate(ctxt);
				}
			}
		}

		if (failOnUnhandled)
		{
			return VdmRuntimeError.abort(location, abortNumber, "'not yet specified' "
					+ type + " reached", ctxt);
		}
		return null;

	}

	@Override
	public Value caseANotYetSpecifiedExp(ANotYetSpecifiedExp node, Context ctxt)
			throws AnalysisException
	{
		return evalDelegatedANotYetSpecified(node, node.getLocation(), 4024, "expression", true, ctxt);
	}

	private Value get_file_pos(Context ctxt)
	{
		try
		{
			ValueList tuple = new ValueList();
			Context outer = ctxt.getRoot().outer;
			RootContext root = outer.getRoot();

			tuple.add(new SeqValue(ctxt.location.getFile().getPath()));
			tuple.add(new NaturalOneValue(ctxt.location.getStartLine()));
			tuple.add(new NaturalOneValue(ctxt.location.getStartPos()));
			tuple.add(new SeqValue(ctxt.location.getModule()));

			int bra = root.title.indexOf('(');

			if (bra > 0)
			{
				tuple.add(new SeqValue(root.title.substring(0, bra)));
			} else
			{
				tuple.add(new SeqValue(""));
			}

			return new TupleValue(tuple);
		} catch (ValueException e)
		{
			throw new ContextException(e, ctxt.location);
		} catch (Exception e)
		{
			throw new ContextException(4076, e.getMessage(), ctxt.location, ctxt);
		}
	}
}
