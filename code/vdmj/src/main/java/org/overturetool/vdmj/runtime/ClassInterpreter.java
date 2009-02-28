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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionSet;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.StatementReader;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.typechecker.PublicClassEnvironment;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;

/**
 * The VDM++ interpreter.
 */

public class ClassInterpreter extends Interpreter
{
	private final ClassList classes;
	private ClassDefinition defaultClass;
	private NameValuePairMap createdValues;
	private DefinitionSet createdDefinitions;

	/**
	 * Create an Interpreter.
	 * @throws Exception
	 */

	public ClassInterpreter(ClassList classes) throws Exception
	{
		this.classes = classes;
		init();

		if (classes.isEmpty())
		{
			setDefaultName(null);
		}
		else
		{
			setDefaultName(classes.get(0).name.name);
		}
	}

	@Override
	public void setDefaultName(String cname) throws Exception
	{
		if (cname == null)
		{
			defaultClass = new ClassDefinition();
			classes.add(defaultClass);
		}
		else
		{
    		for (ClassDefinition c: classes)
    		{
    			if (c.name.name.equals(cname))
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
		return defaultClass.name.name;
	}

	@Override
	public String getDefaultFilename()
	{
		return defaultClass.name.location.file;
	}

	public ClassList getClasses()
	{
		return classes;
	}

	@Override
	public String getInitialContext()
	{
		return initialContext.toString() +
			(createdValues.isEmpty() ? "" :
				Utils.listToString("", createdValues.asList(), "\n", "\n"));
	}

	@Override
	public Environment getGlobalEnvironment()
	{
		return new PublicClassEnvironment(classes);
	}

	/**
	 * Initialize the initial environment.
	 *
	 * @throws Exception
	 */

	@Override
	public void init()
	{
		initialContext = classes.initialize();
		createdValues = new NameValuePairMap();
		createdDefinitions = new DefinitionSet();
	}

	@Override
	public Expression parseExpression(String line, String module)
		throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(line, Dialect.VDM_PP);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(module);
		return reader.readExpression();
	}

	@Override
	public Statement parseStatement(String line, String module)
		throws Exception
	{
		LexTokenReader ltr = new LexTokenReader(line, Dialect.VDM_PP);
		StatementReader sr = new StatementReader(ltr);
		sr.setCurrentModule(module);
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

	public Value execute(Expression expr)
	{
		mainContext = new StateContext(
			defaultClass.name.location, "global static scope");

		mainContext.putAll(initialContext);
		mainContext.putAll(createdValues);
		mainContext.threadState.init();

		return expr.eval(mainContext);
	}

	/**
	 * Parse the line passed, type check it and evaluate it as an expression
	 * in the initial context.
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
		Environment created =
			new FlatCheckedEnvironment(createdDefinitions.asList(), env);

		typeCheck(expr, created, true);
		return execute(expr);
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
		LexNameToken classname =
			new LexNameToken("CLASS", ctxt.location.module, ctxt.location);
		ClassDefinition me = (ClassDefinition)classes.findType(classname);
		PublicClassEnvironment globals = new PublicClassEnvironment(classes);
		Environment env = new PrivateClassEnvironment(me, globals);
		typeCheck(expr, env, false);

		ctxt.threadState.init();
		return expr.eval(ctxt);
	}

	@Override
	public ClassDefinition findClass(String classname)
	{
		LexNameToken name = new LexNameToken("CLASS", classname, null);
		return (ClassDefinition)classes.findType(name);
	}

	@Override
	public Value findGlobal(LexNameToken name)
	{
		// The name will not be type-qualified, so we can't use the usual
		// findName methods

		for (ClassDefinition c: classes)
		{
			for (Definition d: c.definitions)
			{
				if (d.isFunctionOrOperation())
				{
					NameValuePairList nvpl = d.getNamedValues(initialContext);

					for (NameValuePair n: nvpl)
					{
						if (n.name.matches(name))
						{
							return n.value;
						}
					}
				}
			}

			for (Definition d: c.allInheritedDefinitions)
			{
				if (d.isFunctionOrOperation())
				{
					NameValuePairList nvpl = d.getNamedValues(initialContext);

					for (NameValuePair n: nvpl)
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
	public Statement findStatement(String file, int lineno)
	{
		for (ClassDefinition c: classes)
		{
			if (c.name.location.file.equals(file))
			{
    			Statement stmt = c.findStatement(lineno);

    			if (stmt != null)
    			{
    				return stmt;
    			}
			}
		}

		return null;
	}

	@Override
	public Expression findExpression(String file, int lineno)
	{
		for (ClassDefinition c: classes)
		{
			if (c.name.location.file.equals(file))
			{
    			Expression exp = c.findExpression(lineno);

    			if (exp != null)
    			{
    				return exp;
    			}
			}
		}

		return null;
	}

	public void create(String var, String exp) throws Exception
	{
		Expression expr = parseExpression(exp, getDefaultName());
		Environment env = getGlobalEnvironment();
		Environment created =
			new FlatCheckedEnvironment(createdDefinitions.asList(), env);

		Type type = typeCheck(expr, created, true);
		Value v = execute(exp);

		LexLocation location = defaultClass.location;
		LexNameToken n = new LexNameToken(defaultClass.name.name, var, location);

		createdValues.put(n, v);
		createdDefinitions.add(new LocalDefinition(location, n, NameScope.LOCAL, type));
	}

	@Override
	public ProofObligationList getProofObligations()
	{
		return classes.getProofObligations();
	}

	public List<Object> runtrace(String classname, String... statements)
	{
		return runtrace(classname, Arrays.asList(statements));
	}

	public List<Object> runtrace(String classname, List<String> statements)
	{
		List<Object> list = new Vector<Object>();
		ClassDefinition classdef = findClass(classname);

		if (classdef == null)
		{
			list.add("Class " + classname + " not found");
			return list;
		}

		Environment env = new FlatEnvironment(
			classdef.getSelfDefinition(),
			new PrivateClassEnvironment(classdef, getGlobalEnvironment()));

		ObjectValue object = null;

		try
		{
			object = classdef.newInstance(null, null, initialContext);
		}
		catch (ValueException e)
		{
			list.add(e.getMessage());
			return list;
		}

		Context ctxt = new ObjectContext(
				classdef.name.location, classdef.name.name + "()",
				initialContext, object);

		ctxt.put(classdef.name.getSelfName(), object);

		for (String statement: statements)
		{
			try
			{
				Statement s = parseStatement(statement, classname);
				typeCheck(s, env, false);

				if (TypeChecker.getErrorCount() != 0)
				{
					List<VDMError> errors = TypeChecker.getErrors();
					list.add(Utils.listToString(errors, " and "));
					break;
				}
				else
				{
					list.add(s.eval(ctxt));
				}
			}
			catch (Exception e)
			{
				list.add(e.getMessage());
				break;
			}
		}

		return list;
	}

	public List<Object> runtrace(String classname, CallSequence statements)
	{
		List<Object> list = new Vector<Object>();
		ClassDefinition classdef = findClass(classname);

		if (classdef == null)
		{
			list.add("Class " + classname + " not found");
			return list;
		}

		Environment env = new FlatEnvironment(
			classdef.getSelfDefinition(),
			new PrivateClassEnvironment(classdef, getGlobalEnvironment()));

		ObjectValue object = null;

		try
		{
			object = classdef.newInstance(null, null, initialContext);
		}
		catch (ValueException e)
		{
			list.add(e.getMessage());
			return list;
		}

		Context ctxt = new ObjectContext(
				classdef.name.location, classdef.name.name + "()",
				initialContext, object);

		ctxt.put(classdef.name.getSelfName(), object);

		for (CallObjectStatement statement: statements)
		{
			try
			{
				typeCheck(statement, env, false);

				if (TypeChecker.getErrorCount() != 0)
				{
					List<VDMError> errors = TypeChecker.getErrors();
					list.add(Utils.listToString(errors, " and "));
					break;
				}
				else
				{
					list.add(statement.eval(ctxt));
				}
			}
			catch (Exception e)
			{
				list.add(e.getMessage());
				break;
			}
		}

		return list;
	}
}
