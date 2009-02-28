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

package org.overturetool.vdmj.runtime;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.StatementReader;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.ModuleEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;

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
		init();

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
	public String getDefaultFilename()
	{
		return defaultModule.name.location.file;
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

	/**
	 * Initialize the initial environment. This includes the initialization
	 * of the state variables of all modules, if any.
	 *
	 * @throws Exception
	 */

	@Override
	public void init()
	{
		initialContext = modules.initialize();
	}

	@Override
	public Expression parseExpression(String line, String module) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(line, Dialect.VDM_SL);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(getDefaultName());
		return reader.readExpression();
	}

	@Override
	public Statement parseStatement(String line, String module) throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(line, Dialect.VDM_SL);
		StatementReader sr = new StatementReader(ltr);
		sr.setCurrentModule(getDefaultName());
		return sr.readStatement();
	}

	@Override
	public Type typeCheck(Expression expr, Environment env, boolean raise)
		throws Exception
	{
		TypeChecker.clearErrors();
		Type type = expr.typeCheck(env, null, NameScope.NAMESANDSTATE);

		if (raise && TypeChecker.getErrorCount() > 0)
		{
			TypeChecker.printErrors(Console.out);
			throw new Exception("Type checking errors");
		}

		return type;
	}

	@Override
	public Type typeCheck(Statement stmt, Environment env, boolean raise)
		throws Exception
	{
		TypeChecker.clearErrors();
		Type type = stmt.typeCheck(env, NameScope.NAMESANDSTATE);

		if (raise && TypeChecker.getErrorCount() > 0)
		{
			TypeChecker.printErrors(Console.out);
			throw new Exception("Type checking errors");
		}

		return type;
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
	public Value execute(String line) throws Exception
	{
		Expression expr = parseExpression(line, getDefaultName());
		Environment env = getGlobalEnvironment();
		typeCheck(expr, env, true);

		Context sctxt = defaultModule.getStateContext();
		mainContext = new StateContext(
			defaultModule.name.location, "interpreter", initialContext, sctxt);
		mainContext.threadState.init();

		return expr.eval(mainContext);
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
		LexIdentifierToken name =
			new LexIdentifierToken(ctxt.location.module, false, ctxt.location);
		Module me = modules.findModule(name);
		Environment env = new ModuleEnvironment(me);
		typeCheck(expr, env, false);

		ctxt.threadState.init();
		return expr.eval(ctxt);
	}

	@Override
	public Module findModule(String module)
	{
		LexIdentifierToken name = new LexIdentifierToken(module, false, null);
		return modules.findModule(name);
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
	public Statement findStatement(String file, int lineno)
	{
		for (Module m: modules)
		{
			if (m.name.location.file.equals(file))
			{
				return m.findStatement(lineno);
			}
		}

		return null;
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
	public Expression findExpression(String file, int lineno)
	{
		for (Module m: modules)
		{
			if (m.name.location.file.equals(file))
			{
				return m.findExpression(lineno);
			}
		}

		return null;
	}

	@Override
	public ProofObligationList getProofObligations()
	{
		return modules.getProofObligations();
	}
}
