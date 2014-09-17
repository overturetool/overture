/*
 * #%~
 * The VDM parser
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.parser.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.test.framework.BaseTestSuite;

public class StatementTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Statement TestSuite";
		String root = "src\\test\\resources\\statements";
		TestSuite test = createTestCompleteFile(name, root + "\\complete", StatementTestCase.class, "");
		TestSuite test2 = createTestSingleLineFile(name, root + "\\singleline", StatementTestCase.class, "");
		@SuppressWarnings("unchecked")
		Enumeration<Test> e = test2.tests();
		while (e.hasMoreElements())
		{
			test.addTest(e.nextElement());
		}
		return test;
	}

}
