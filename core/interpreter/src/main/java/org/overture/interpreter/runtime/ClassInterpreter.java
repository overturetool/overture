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
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;
import org.overture.ast.util.definitions.ClassList;
import org.overture.config.Settings;
import org.overture.interpreter.annotations.INAnnotation;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.InterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.messages.rtlog.RTThreadCreateMessage;
import org.overture.interpreter.messages.rtlog.RTThreadKillMessage;
import org.overture.interpreter.messages.rtlog.RTThreadSwapMessage;
import org.overture.interpreter.messages.rtlog.RTThreadSwapMessage.SwapType;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.CTMainThread;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.scheduler.InitThread;
import org.overture.interpreter.scheduler.MainThread;
import org.overture.interpreter.scheduler.RunState;
import org.overture.interpreter.scheduler.Signal;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.values.BUSValue;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.Value;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.PublicClassEnvironment;
import org.overture.typechecker.assistant.definition.PDefinitionSet;

/**
 * The VDM++ interpreter.
 */

public class ClassInterpreter extends Interpreter
{
	private final ClassListInterpreter classes;
	private SClassDefinition defaultClass;
	private NameValuePairMap createdValues;
	private PDefinitionSet createdDefinitions;

	public ClassInterpreter(ClassList classes) throws Exception
	{
		this(new InterpreterAssistantFactory(), classes);
	}

	public ClassInterpreter(IInterpreterAssistantFactory assistantFactory,
			ClassList classes) throws Exception
	{
		super(assistantFactory);
		this.classes = new ClassListInterpreter(classes);
		this.createdValues = new NameValuePairMap();
		this.createdDefinitions = assistantFactory.createPDefinitionSet();

		if (classes.isEmpty())
		{
			setDefaultName(null);
		} else
		{
			setDefaultName(classes.get(0).getName().getName());
		}
	}

	@Override
	public void setDefaultName(String cname) throws Exception
	{
		if (cname == null)
		{
			defaultClass = AstFactory.newAClassClassDefinition();
			classes.add(defaultClass);
		} else
		{
			for (SClassDefinition c : classes)
			{
				if (c.getName().getName().equals(cname))
				{
					defaultClass = c;
					return;
				}
			}

			throw new Exception("Class " + cname + " not loaded");
		}
	}

	@Override
	public String getDefaultName()
	{
		return defaultClass.getName().getName();
	}

	@Override
	public File getDefaultFile()
	{
		return defaultClass.getName().getLocation().getFile();
	}

	@Override
	public Set<File> getSourceFiles()
	{
		return classes.getSourceFiles();
	}

	public ClassListInterpreter getClasses()
	{
		return classes;
	}

	@Override
	public String getInitialContext()
	{
		return initialContext.toString()
				+ (createdValues.isEmpty() ? ""
						: Utils.listToString("", createdValues.asList(), "\n", "\n"));
	}

	@Override
	public Environment getGlobalEnvironment()
	{
		return new PublicClassEnvironment(assistantFactory, classes, null);
	}
	
	@Override
	public Environment getGlobalEnvironment(String module)
	{
		return getGlobalEnvironment(); 
	}

	@Override
	public void init(DBGPReader dbgp)
	{
		BasicSchedulableThread.terminateAll();
		VdmRuntime.initialize();

		RuntimeValidator.init(this);
		InitThread iniThread = new InitThread(Thread.currentThread());
		BasicSchedulableThread.setInitialThread(iniThread);

		scheduler.init();
		SystemClock.init();
		CPUValue.init(scheduler, assistantFactory);
		BUSValue.init();
		ObjectValue.init();

		logSwapIn();
		initialContext = classes.createInitialContext(assistantFactory);
		classes.initialize(initialContext, assistantFactory, dbgp);
		classes.systemInit(scheduler, dbgp, initialContext);
		Annotation.init(INAnnotation.class, initialContext);
		logSwapOut();

		createdValues = new NameValuePairMap();
		createdDefinitions = assistantFactory.createPDefinitionSet();

		scheduler.reset(); // Required before a run, as well as init above
		BUSValue.start(); // Start any BUS threads first...
	}

	@Override
	public void traceInit(DBGPReader dbgp)
	{
		BasicSchedulableThread.terminateAll();
		scheduler.reset();

		SystemClock.init();
		initialContext = classes.createInitialContext(assistantFactory);
		classes.initialize(initialContext, assistantFactory, dbgp);
		createdValues = new NameValuePairMap();
		createdDefinitions = assistantFactory.createPDefinitionSet();
	}

	@Override
	protected PExp parseExpression(String line, String module) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(line, Settings.dialect, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(module);
		PExp ast = reader.readExpression();
		LexToken end = ltr.getLast();
		
		if (!end.is(VDMToken.EOF))
		{
			throw new ParserException(2330, "Tokens found after expression at " + end, new LexLocation(), 0);
		}
		
		return ast;
	}

	private Value execute(PExp expr, DBGPReader dbgp) throws Exception
	{
		Context mainContext = new StateContext(assistantFactory, defaultClass.getName().getLocation(), "global static scope");

		mainContext.putAll(initialContext);
		mainContext.putAll(createdValues);
		mainContext.setThreadState(dbgp, CPUValue.vCPU);
		clearBreakpointHits();

		// scheduler.reset();

		InitThread iniThread = new InitThread(Thread.currentThread());
		BasicSchedulableThread.setInitialThread(iniThread);

		MainThread main = new MainThread(expr, mainContext);
		main.start();
		scheduler.start(main);

		if (Settings.dialect == Dialect.VDM_RT && RTLogger.getLogSize() > 0)
		{
			RTLogger.dump(true);
		}

		RuntimeValidator.stop();

		return main.getResult(); // Can throw ContextException
	}

	/**
	 * Parse the line passed, type check it and evaluate it as an expression in the initial context.
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
		Environment created = new FlatCheckedEnvironment(assistantFactory, createdDefinitions.asList(), env, NameScope.NAMESANDSTATE);

		typeCheck(expr, created);
		return execute(expr, dbgp);
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
		PublicClassEnvironment globals = new PublicClassEnvironment(assistantFactory, classes, null);
		Environment env = new PrivateClassEnvironment(assistantFactory, defaultClass, globals);

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
	public SClassDefinition findClass(String classname)
	{
		LexNameToken name = new LexNameToken("CLASS", classname, null);
		return (SClassDefinition) assistantFactory.createSClassDefinitionAssistant().findType(classes, name);
	}

	@Override
	protected ANamedTraceDefinition findTraceDefinition(LexNameToken name)
	{
		PDefinition d = assistantFactory.createSClassDefinitionAssistant().findName(classes, name, NameScope.NAMESANDSTATE);

		if (d == null || !(d instanceof ANamedTraceDefinition))
		{
			return null;
		}

		return (ANamedTraceDefinition) d;
	}

	@Override
	public Value findGlobal(LexNameToken name)
	{
		// The name will not be type-qualified, so we can't use the usual
		// findName methods

		for (SClassDefinition c : classes)
		{
			for (PDefinition d : c.getDefinitions())
			{
				if (assistantFactory.createPDefinitionAssistant().isFunctionOrOperation(d))
				{
					NameValuePairList nvpl = assistantFactory.createPDefinitionAssistant().getNamedValues(d, initialContext);

					for (NameValuePair n : nvpl)
					{
						if (n.name.matches(name))
						{
							return n.value;
						}
					}
				}
			}

			for (PDefinition d : c.getAllInheritedDefinitions())
			{
				if (assistantFactory.createPDefinitionAssistant().isFunctionOrOperation(d))
				{
					NameValuePairList nvpl = assistantFactory.createPDefinitionAssistant().getNamedValues(d, initialContext);

					for (NameValuePair n : nvpl)
					{
						if (n.name.matches(name))
						{
							return n.value;
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public PStm findStatement(File file, int lineno)
	{
		return assistantFactory.createSClassDefinitionAssistant().findStatement(classes, file, lineno);
	}

	@Override
	public PExp findExpression(File file, int lineno)
	{
		return assistantFactory.createSClassDefinitionAssistant().findExpression(classes, file, lineno);
	}

	public void create(String var, String exp) throws Exception
	{
		PExp expr = parseExpression(exp, getDefaultName());
		Environment env = getGlobalEnvironment();
		Environment created = new FlatCheckedEnvironment(assistantFactory, createdDefinitions.asList(), env, NameScope.NAMESANDSTATE);

		PType type = typeCheck(expr, created);
		Value v = execute(exp, null);

		ILexLocation location = defaultClass.getLocation();
		LexNameToken n = new LexNameToken(defaultClass.getName().getName(), var, location);

		createdValues.put(n, v);
		createdDefinitions.add(AstFactory.newALocalDefinition(location, n, NameScope.LOCAL, type));
	}

	@Override
	public ProofObligationList getProofObligations() throws AnalysisException
	{
		return classes.getProofObligations(assistantFactory);
	}

	private void logSwapIn()
	{
		// Show the "system constructor" thread creation

		ISchedulableThread thread = BasicSchedulableThread.getThread(Thread.currentThread());

		RTLogger.log(new RTThreadCreateMessage(thread, CPUValue.vCPU.resource));

		RTLogger.log(new RTThreadSwapMessage(SwapType.In, thread, CPUValue.vCPU.resource, 0, 0));
	}

	private void logSwapOut()
	{
		ISchedulableThread thread = BasicSchedulableThread.getThread(Thread.currentThread());

		RTLogger.log(new RTThreadSwapMessage(SwapType.Out, thread, CPUValue.vCPU.resource, 0, 0));

		RTLogger.log(new RTThreadKillMessage(thread, CPUValue.vCPU.resource));
	}

	@Override
	public Context getInitialTraceContext(ANamedTraceDefinition tracedef,
			boolean debug) throws AnalysisException
	{
		ObjectValue object = null;

		SClassDefinition classdef = tracedef.getClassDefinition();

		// Create a new test object
		object = assistantFactory.createSClassDefinitionAssistant().newInstance(classdef, null, null, initialContext);

		Context ctxt = new ObjectContext(assistantFactory, classdef.getName().getLocation(), classdef.getName().getName()
				+ "()", initialContext, object);

		ctxt.put(classdef.getName().getSelfName(), object);

		return ctxt;
	}

	@Override
	public List<Object> runOneTrace(ANamedTraceDefinition tracedef,
			CallSequence test, boolean debug) throws AnalysisException
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

		// Ensures all threads are terminated for next trace run
		BasicSchedulableThread.signalAll(Signal.TERMINATE);

		while (main.getRunState() != RunState.COMPLETE)
		{
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				break;
			}
		}

		return main.getList();
	}

	@Override
	public PType findType(String typename)
	{
		for (SClassDefinition cDef : classes)
		{
			for (PDefinition def : cDef.getDefinitions())
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
