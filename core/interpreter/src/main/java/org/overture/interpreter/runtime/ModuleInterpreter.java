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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.annotations.Annotation;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PType;
import org.overture.ast.util.modules.ModuleList;
import org.overture.interpreter.annotations.INAnnotation;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.InterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.CTMainThread;
import org.overture.interpreter.scheduler.InitThread;
import org.overture.interpreter.scheduler.MainThread;
import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.util.ModuleListInterpreter;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.Value;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.pub.IProofObligationList;
import org.overture.typechecker.Environment;
import org.overture.typechecker.ModuleEnvironment;

/**
 * The VDM-SL module interpreter.
 */

public class ModuleInterpreter extends Interpreter
{
	/** A list of module definitions in the specification. */
	public final ModuleListInterpreter modules;
	/** The module starting execution. */
	public AModuleModules defaultModule;

	/**
	 * Create an Interpreter from the list of modules passed.
	 * 
	 * @param modules
	 * @throws Exception
	 */
	public ModuleInterpreter(ModuleList modules) throws Exception
	{
		this(new InterpreterAssistantFactory(), modules);
	}

	/**
	 * Create an Interpreter from the list of modules passed.
	 * 
	 * @param assistantFactory
	 * @param modules
	 * @throws Exception
	 */
	public ModuleInterpreter(IInterpreterAssistantFactory assistantFactory,
			ModuleList modules) throws Exception
	{
		super(assistantFactory);
		this.modules = new ModuleListInterpreter(modules);

		if (modules.isEmpty())
		{
			setDefaultName(null);
		} else
		{
			setDefaultName(modules.get(0).getName().getName());
		}
	}

	@Override
	public PStm findStatement(File file, int lineno)
	{
		return assistantFactory.createAModuleModulesAssistant().findStatement(modules, file, lineno);
	}

	@Override
	public PExp findExpression(File file, int lineno)
	{
		return assistantFactory.createAModuleModulesAssistant().findExpression(modules, file, lineno);
	}

	/**
	 * Set the default module to the name given.
	 * 
	 * @param mname
	 *            The name of the new default module.
	 * @throws Exception
	 *             The module name is not known.
	 */

	@Override
	public void setDefaultName(String mname) throws Exception
	{
		if (mname == null)
		{
			defaultModule = AstFactory.newAModuleModules();
		} else
		{
			for (AModuleModules m : modules)
			{
				if (m.getName().getName().equals(mname))
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
		return defaultModule.getName().getName();
	}

	/**
	 * @return The current default module's file name.
	 */

	@Override
	public File getDefaultFile()
	{
		return defaultModule.getName().getLocation().getFile();
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
		return assistantFactory.createAModuleModulesAssistant().getStateContext(defaultModule);
	}

	@Override
	public Environment getGlobalEnvironment()
	{
		return new ModuleEnvironment(assistantFactory, defaultModule);
	}
	
	@Override
	public Environment getGlobalEnvironment(String module)
	{
		return new ModuleEnvironment(assistantFactory, findModule(module));
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
		VdmRuntime.initialize();
		InitThread iniThread = new InitThread(Thread.currentThread());
		BasicSchedulableThread.setInitialThread(iniThread);
		scheduler.init();
		CPUValue.init(scheduler, assistantFactory);
		initialContext = assistantFactory.createModuleListAssistant().createInitialContext(modules);
		assistantFactory.createModuleListAssistant().initialize(initialContext, modules, dbgp);
		Annotation.init(INAnnotation.class, initialContext);
	}

	@Override
	public void traceInit(DBGPReader dbgp)
	{
		scheduler.reset();
		initialContext = assistantFactory.createModuleListAssistant().createInitialContext(modules);
		assistantFactory.createModuleListAssistant().initialize(initialContext, modules, dbgp);
	}

	@Override
	protected PExp parseExpression(String line, String module) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(line, Dialect.VDM_SL, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(getDefaultName());
		PExp ast = reader.readExpression();
		LexToken end = ltr.getLast();
		
		if (!end.is(VDMToken.EOF))
		{
			throw new ParserException(2330, "Tokens found after expression at " + end, new LexLocation(), 0);
		}
		
		return ast;
	}

	/**
	 * Parse the line passed, type check it and evaluate it as an expression in the initial module context (with default
	 * module's state).
	 * 
	 * @param line
	 *            A VDM expression.
	 * @return The value of the expression.
	 * @throws Exception
	 *             Parser, type checking or runtime errors.
	 */

	@Override
	public Value execute(String line, DBGPReader dbgp) throws Exception
	{
		PExp expr = parseExpression(line, getDefaultName());
		Environment env = getGlobalEnvironment();
		typeCheck(expr, env);

		Context mainContext = new StateContext(assistantFactory, defaultModule.getName().getLocation(), "module scope", null, assistantFactory.createAModuleModulesAssistant().getStateContext(defaultModule));

		mainContext.putAll(initialContext);
		mainContext.setThreadState(dbgp, null);
		clearBreakpointHits();

		// scheduler.reset();

		InitThread iniThread = new InitThread(Thread.currentThread());
		BasicSchedulableThread.setInitialThread(iniThread);

		MainThread main = new MainThread(expr, mainContext);
		main.start();
		scheduler.start(main);

		return main.getResult(); // Can throw ContextException
	}

	/**
	 * Parse the line passed, and evaluate it as an expression in the context passed.
	 * 
	 * @param line
	 *            A VDM expression.
	 * @param ctxt
	 *            The context in which to evaluate the expression.
	 * @return The value of the expression.
	 * @throws Exception
	 *             Parser or runtime errors.
	 */

	@Override
	public Value evaluate(String line, Context ctxt) throws Exception
	{
		PExp expr = parseExpression(line, getDefaultName());
		Environment env = new ModuleEnvironment(assistantFactory, defaultModule);

		try
		{
			typeCheck(expr, env);
		} catch (VDMErrorsException e)
		{
			// We don't care... we just needed to type check it.
		}

		ctxt.threadState.init();

		try
		{
			return expr.apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		} catch (Exception e)
		{
			throw e;
		}
	}

	@Override
	public AModuleModules findModule(String module)
	{
		LexIdentifierToken name = new LexIdentifierToken(module, false, null);
		return modules.findModule(name);
	}

	@Override
	protected ANamedTraceDefinition findTraceDefinition(LexNameToken name)
	{
		return modules.findTraceDefinition(name);
	}

	// /**
	// * Find a Statement in the given file that starts on the given line.
	// * If there are none, return null.
	// *
	// * @param file The file name to search.
	// * @param lineno The line number in the file.
	// * @return A Statement starting on the line, or null.
	// */
	//
	// @Override
	// public PStm findStatement(File file, int lineno)
	// {
	// return modules.findStatement(file, lineno);
	// }

	// /**
	// * Find an Expression in the given file that starts on the given line.
	// * If there are none, return null.
	// *
	// * @param file The file name to search.
	// * @param lineno The line number in the file.
	// * @return An Expression starting on the line, or null.
	// */
	//
	// @Override
	// public PExp findExpression(File file, int lineno)
	// {
	// return modules.findExpression(file, lineno);
	// }

	@Override
	public IProofObligationList getProofObligations() throws AnalysisException
	{
		return assistantFactory.createModuleListAssistant().getProofObligations(modules);
	}

	@Override
	public Context getInitialTraceContext(ANamedTraceDefinition tracedef,
			boolean debug) throws ValueException
	{
		Context mainContext = new StateContext(assistantFactory, defaultModule.getName().getLocation(), "module scope", initialContext, assistantFactory.createAModuleModulesAssistant().getStateContext(defaultModule));

		mainContext.putAll(initialContext);
		mainContext.setThreadState(mainContext.threadState.dbgp, CPUValue.vCPU);

		return mainContext;
	}

	@Override
	public List<Object> runOneTrace(ANamedTraceDefinition tracedef,
			CallSequence test, boolean debug)
	{
		List<Object> list = new Vector<Object>();
		Context ctxt = null;

		try
		{
			ctxt = getInitialTraceContext(tracedef, debug);

		} catch (ValueException e)
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
	public PType findType(String typename)
	{
		for (AModuleModules module : modules)
		{
			for (PDefinition def : module.getDefs())
			{
				if (def instanceof ATypeDefinition)
				{
					if (def.getName().equals(typename))
					{
						return def.getType();
					}
				}
			}
		}
		return null;
	}
}
