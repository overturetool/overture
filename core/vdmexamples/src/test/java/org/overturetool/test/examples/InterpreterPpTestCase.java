/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others. Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version. Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public
 * License along with Overture. If not, see <http://www.gnu.org/licenses/>. The Overture Tool web-site:
 * http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.examples;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.framework.examples.VdmReadme;
import org.overturetool.test.framework.examples.VdmReadme.ResultStatus;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.IResultCombiner;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.StderrRedirector;
import org.overturetool.vdmj.messages.StdoutRedirector;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ContextException;

public class InterpreterPpTestCase extends TypeCheckPpTestCase
{
	public InterpreterPpTestCase()
	{
	}

	public InterpreterPpTestCase(File file)
	{
		super(file);
	}

	@Override
	public void test() throws Exception
	{
		if (mode == ContentModed.None)
		{
			return;
		}

		VdmReadme settings = getReadme();
		
		if(settings.getExpectedResult()==ResultStatus.NO_CHECK|| settings.getExpectedResult()==ResultStatus.NO_ERROR_SYNTAX|| settings.getExpectedResult()==ResultStatus.NO_ERROR_TYPE_CHECK)
		{
			return;
		}
		
		Set<Result<String>> results = new HashSet<Result<String>>();
		for (String expression : settings.getEntryPoints())
		{
			results.add(interpret(expression));
		}
		@SuppressWarnings("unchecked")
		Result<String> res = mergeResults(results, new IResultCombiner<String>()
		{

			public String combine(String a, String b)
			{
				return a + b;
			}
		});

		compareResults(res, "interpreter.result");
	}

	protected Result<String> interpret(String expression) throws Exception
	{
		Result<ClassList> res = typeCheck();
String result = null;
		
		if(res.errors.isEmpty())
		{

		ClassInterpreter intepreter = new ClassInterpreter(res.result);
		intepreter.init(null);
		// if(interpreter.initialContext!=null)
		// {
		// interpreter.initialContext.size()
		// }
		expression = expression.trim();
		String defaultModule = null;
		if (expression.startsWith("new"))
		{
			defaultModule = expression.substring(expression.indexOf(' ')).trim();
			defaultModule = defaultModule.substring(0, defaultModule.indexOf('('));
		} else
		{
			defaultModule = expression.substring(0, expression.indexOf('`')).trim();
		}
//		System.out.println("Default is: "+ defaultModule);
		intepreter.setDefaultName(defaultModule);
//		Value value = interpreter.execute(expression, null);
//		return new Result<String>(value == null ? null : value.toString(), new HashSet<IMessage>(), new HashSet<IMessage>());
		
		try{
			result = intepreter.execute(expression, null).toString();
		}catch(OutOfMemoryError e)
		{
			result = e.getMessage();
		}catch(ContextException e)
		{
			result = e.getMessage();
		}catch(VDMErrorsException e)
		{
			result = e.getMessage();
		}}else
		{
			result = "Silent failure. Type check faild";
		}
		
		
		return new Result<String>(result,new HashSet<IMessage>(),new HashSet<IMessage>());
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.baseDir = file;
		Console.out = new StdoutRedirector(new OutputStreamWriter(new NullOutputStream()));
		Console.err = new StderrRedirector(new OutputStreamWriter(new NullOutputStream()));
	}

	/** Writes to nowhere */
	public class NullOutputStream extends OutputStream
	{
		@Override
		public void write(int b) throws IOException
		{
		}
	}
}
