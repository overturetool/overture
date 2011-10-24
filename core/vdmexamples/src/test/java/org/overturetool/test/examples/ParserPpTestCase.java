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

import org.overturetool.test.examples.vdmj.ParserProxy;
import org.overturetool.test.examples.vdmj.VdmjFactories;
import org.overturetool.test.framework.examples.ExamplesTestCase;
import org.overturetool.test.framework.examples.Result;
import org.overturetool.test.framework.examples.VdmReadme;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.syntax.ClassReader;

public class ParserPpTestCase extends ExamplesTestCase
{
	protected String extension = "vdmpp";

	public ParserPpTestCase()
	{
	}

	public ParserPpTestCase(File file)
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

		Result<List<ClassDefinition>> res = parse();
		compareResults(res.warnings, res.errors, res.result,"parser.results");
	}

	protected Result<List<ClassDefinition>> parse() throws Exception
	{
		ParserProxy<ClassReader, List<ClassDefinition>> parser = new ParserProxy<ClassReader, List<ClassDefinition>>(VdmjFactories.vdmPpParserfactory, getSpecFiles(extension, file));
		Result<List<ClassDefinition>> res = mergeResults(parser.parse(), VdmjFactories.vdmPpParserResultCombiner);
		return res;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		VdmReadme settings = getReadme();
		if (settings != null)
		{
			Settings.release = Release.lookup(settings.getLanguageVersion());
		}
	}
}
