/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.tests.framework;

import java.io.File;
import java.io.PrintWriter;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.Properties;
import org.overture.test.framework.TestResourcesResultTestCase4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class TypeCheckTestCase extends
		TestResourcesResultTestCase4<Boolean>
{

	private static final String TESTS_TC_PROPERTY_PREFIX = "testing.tc.override.";

	public TypeCheckTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	public void encodeResult(Boolean result, Document doc,
			Element resultElement)
	{

	}

	public Boolean decodeResult(Node node)
	{
		return null;
	}

	@Override
	protected boolean assertEqualResults(Boolean expected, Boolean actual,
			PrintWriter out)
	{
		// not used by type check
		return true;
	}

	protected void configureResultGeneration()
	{
		LexLocation.absoluteToStringLocation = false;
		if (System.getProperty(TESTS_TC_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
				&& System.getProperty(TESTS_TC_PROPERTY_PREFIX
						+ getPropertyId()) != null)
		{
			Properties.recordTestResults = true;
		}

	}

	protected void unconfigureResultGeneration()
	{
		Properties.recordTestResults = false;
	}

	protected abstract String getPropertyId();
}
