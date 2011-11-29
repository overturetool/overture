/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.examples;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.framework.examples.VdmReadme;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.IResultCombiner;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.runtime.ClassInterpreter;

public class InterpreterRtTestCase extends TypeCheckRtTestCase
{
	public InterpreterRtTestCase()
	{
	}

	public InterpreterRtTestCase(File file)
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
		Set<Result<String>> results = new HashSet<Result<String>>();
		for (String expression : settings.getEntryPoints())
		{
			results.add( interpret(expression));
		}
		@SuppressWarnings("unchecked")
		Result<String> res = mergeResults(results, new IResultCombiner<String>()
		{

			public String combine(String a, String b)
			{
				return a+b;
			}
		});

		compareResults(res,"interpreter.result");
	}
	
	protected Result<String> interpret(String expression) throws Exception
	{
		Result<ClassList> res = typeCheck();
		
		ClassInterpreter intepreter = new ClassInterpreter(res.result);
		return new Result<String>(intepreter.execute(expression, null).toString(),new HashSet<IMessage>(),new HashSet<IMessage>());
	}

	

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_RT;
		Settings.baseDir = file;
	}
}
