/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.runtime;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.ast.util.modules.ModuleList;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.InitThread;
import org.overture.interpreter.values.CPUValue;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;


/**
 * The VDM-SL module interpreter.
 */

public class ModuleInterpreter extends Interpreter
{
	/** A list of module definitions in the specification. */
	public final ModuleList modules;
	/** The module starting execution. */
	public Module defaultModule;

	/**
	 * Create an Interpreter from the list of modules passed.
	 *
	 * @param modules
	 * @throws Exception
	 */

	public ModuleInterpreter(ModuleList modules) throws Exception
	{
		this.modules = modules;

		if (modules.isEmpty())
		{
			setDefaultName(null);
		}
		else
		{
			setDefaultName(modules.get(0).name.name);
		}
	}

	/**
	 * Set the default module to the name given.
	 *
	 * @param mname The name of the new default module.
	 * @throws Exception The module name is not known.
	 */

	@Override
	public void setDefaultName(String mname) throws Exception
	{
		if (mname == null)
		{
			defaultModule = new Module();
		}
		else
		{
			for (Module m: modules)
			{
				if (m.name.name.equals(mname))
				{
					defaultModule = m;
					return;
				}
			}

			throw new Exception("Module " + mname + " not loaded");
		}
	}

	/**
	 * @return The current default module name.
	 */

	@Override
	public String getDefaultName()
	{
		return defaultModule.name.name;
	}

	/**
	 * @return The current default module's file name.
	 */

	@Override
	public File getDefaultFile()
	{
		return defaultModule.name.location.file;
	}

	@Override
	public Set<File> getSourceFiles()
	{
		return modules.getSourceFiles();
	}

	/**
	 * @return The state context for the current default module.
	 */

	public Context getStateContext()
	{
		return defaultModule.getStateContext();
	}

	@Override
	public Environment getGlobalEnvironment()
	{
		return new ModuleEnvironment(defaultModule);
	}

	/**
	 * @return The list of loaded modules.
	 */

	public ModuleList getModules()
	{
		return modules;
	}

	@Override
	public void init(DBGPReader dbgp)
	{
		InitThread iniThread = new InitThread(Thread.currentThread());
		BasicSchedulableThread.setInitialThread(iniThread);
		scheduler.init();
		CPUValue.init(scheduler);
		initialContext = modules.initialize(dbgp);
	}

	@Override
	public void traceInit(DBGPReader dbgp)
	{
		scheduler.reset();
		initialContext = modules.initialize(dbgp);
	}

	@Override
	protected Expression parseExpression(String line, String module) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(line, Dialect.VDM_SL, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(getDefaultName());
		return reader.readExpression();
	}

	/**
	 * Parse the line passed, type check it and evaluate it as an expression
	 * in the initial module context (with default module's state).
	 *
	 * @param line A VDM expression.
	 * @return The value of the expression.
	 * @throws Exception Parser, type checking or runtime errors.
	 */

	@Override
	public Value execute(String line, DBGPReader dbgp) throws Exception
	{
		Expression expr = parseExpression(line, getDefaultName());
		Environment env = getGlobalEnvironment();
		typeCheck(expr, env);

		Context mainContext = new StateContext(defaultModule.name.location,
				"module scope",	null, defaultModule.getStateContext());

		mainContext.putAll(initialContext);
		mainContext.setThreadState(dbgp, null);
		clearBreakpointHits();

		// scheduler.reset();

		InitThread iniThread = new InitThread(Thread.currentThread());
		BasicSchedulableThread.setInitialThread(iniThread);

		MainThread main = new MainThread(expr, mainContext);
		main.start();
		scheduler.start(main);

		return main.getResult();	// Can throw ContextException
	}

	/**
	 * Parse the line passed, and evaluate it as an expression in the context
	 * passed.
	 *
	 * @param line A VDM expression.
	 * @param ctxt The context in which to evaluate the expression.
	 * @return The value of the expression.
	 * @throws Exception Parser or runtime errors.
	 */

	@Override
	public Value evaluate(String line, Context ctxt) throws Exception
	{
		Expression expr = parseExpression(line, getDefaultName());
		Environment env = new ModuleEnvironment(defaultModule);

		try
		{
			typeCheck(expr, env);
		}
		catch (VDMErrorsException e)
		{
			// We don't care... we just needed to type check it.
		}

		ctxt.threadState.init();

		return expr.eval(ctxt);
	}

	@Override
	public Module findModule(String module)
	{
		LexIdentifierToken name = new LexIdentifierToken(module, false, null);
		return modules.findModule(name);
	}

	@Override
	protected NamedTraceDefinition findTraceDefinition(LexNameToken name)
	{
		return modules.findTraceDefinition(name);
	}

	/**
	 * Find a Statement in the given file that starts on the given line.
	 * If there are none, return null.
	 *
	 * @param file The file name to search.
	 * @param lineno The line number in the file.
	 * @return A Statement starting on the line, or null.
	 */

	@Override
	public Statement findStatement(File file, int lineno)
	{
		return modules.findStatement(file, lineno);
	}

	/**
	 * Find an Expression in the given file that starts on the given line.
	 * If there are none, return null.
	 *
	 * @param file The file name to search.
	 * @param lineno The line number in the file.
	 * @return An Expression starting on the line, or null.
	 */

	@Override
	public Expression findExpression(File file, int lineno)
	{
		return modules.findExpression(file, lineno);
	}

	@Override
	public ProofObligationList getProofObligations()
	{
		return modules.getProofObligations();
	}

	@Override
	public Context getInitialTraceContext(NamedTraceDefinition tracedef, boolean debug) throws ValueException
	{
		return initialContext;
	}



	@Override
	public List<Object> runOneTrace(
			NamedTraceDefinition tracedef, CallSequence test, boolean debug)
	{
		List<Object> list = new Vector<Object>();
		Context ctxt = null;

		try
		{
			ctxt = getInitialTraceContext(tracedef, debug);

		}
		catch (ValueException e)
		{
			list.add(e.getMessage());
			return list;
		}

		clearBreakpointHits();

		// scheduler.reset();
		CTMainThread main = new CTMainThread(test, ctxt, debug);
		main.start();
		scheduler.start(main);

		return main.getList();
	}

	@Override
	public Type findType(String typename)
	{
		for (Module module : modules)
		{
			for (Definition def : module.defs)
			{
				if(def instanceof TypeDefinition)
				{
					if(def.getName().equals(typename))
					{
						return def.getType();
					}
				}
			}
		}
		return null;
	}
}
