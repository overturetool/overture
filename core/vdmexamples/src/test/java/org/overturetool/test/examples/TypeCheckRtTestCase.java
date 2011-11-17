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
import java.util.List;

import org.overturetool.test.examples.testsuites.VdmTypeCheckExamplesTestSuite;
import org.overturetool.test.examples.vdmj.TypeCheckerProxy;
import org.overturetool.test.examples.vdmj.VdmjFactories.IMessageConverter;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMMessage;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class TypeCheckRtTestCase extends ParserRtTestCase
{
	public TypeCheckRtTestCase()
	{
	}

	public TypeCheckRtTestCase(File file)
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

		Result<ClassList> res = typeCheck();
		if(VdmTypeCheckExamplesTestSuite.failTestWithTcErrors && res.errors.size()>0)
		{
			fail("TC completed with errors:\n"+res);
		}
		

		compareResults(res.warnings, res.errors, res.result,"typechecker.result");
	}

	public Result<ClassList> typeCheck() throws Exception, ParserException,
			LexException
	{
		Result<List<ClassDefinition>> parserRes =parse();

		ClassList classes = new ClassList();
		if(parserRes.result==null)
		{
			fail("No output from parser");
		}
		classes.addAll(parserRes.result);
		classes.add(new CPUClassDefinition());
		classes.add(new BUSClassDefinition());

		Result<ClassList> res = TypeCheckerProxy.typeCheck(new ClassTypeChecker(classes), new IMessageConverter()
		{

			public IMessage convertMessage(Object m)
			{
				VDMMessage msg = (VDMMessage) m;
				return new Message(msg.location.file.getName(),msg.number, msg.location.startLine, msg.location.endPos, msg.message);
			}
		});

		res.result = classes;
		return res;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TypeChecker.clearErrors();
	}
}
