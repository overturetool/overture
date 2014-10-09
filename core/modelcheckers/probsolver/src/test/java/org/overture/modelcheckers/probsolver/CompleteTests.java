/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;

@RunWith(value = Parameterized.class)
public class CompleteTests extends AllTest
{
	public CompleteTests(Dialect dialect, File source, String operationName,
			String name)
	{
		super(dialect, source, operationName, name);
	}

	@Parameters(name = "{3}")
	public static Collection<Object[]> getData()
	{
		String root = "src/test/resources/modules/complete/";

		Collection<Object[]> tests = new LinkedList<Object[]>();

		tests.addAll(getTests(new File(root), "hotel", "soccer-inlined-functions"));

		return tests;
	}

	@Override
	protected String getPropertyId()
	{
		return "complete";
	}
}
