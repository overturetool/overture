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
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.syntax.ModuleReader;

public class ParserSlTestCase extends ExamplesTestCase
{
	public ParserSlTestCase()
	{
	}

	public ParserSlTestCase(File file)
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

		Result<List<Module>> res = parse();
		compareResults(res.warnings, res.errors, res.result,"parser.result");
	}

	protected Result<List<Module>> parse() throws Exception
	{
		ParserProxy<ModuleReader, List<Module>> parser = new ParserProxy<ModuleReader, List<Module>>(VdmjFactories.vdmSlParserfactory, getSpecFiles("vdmsl", file));
		@SuppressWarnings("unchecked")
		Result<List<Module>> res = mergeResults(parser.parse(), VdmjFactories.vdmSlParserResultCombiner);
		return res;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		VdmReadme settings = getReadme();
		if (settings != null)
		{
			Settings.release = Release.lookup(settings.getLanguageVersion());
		}
	}
}
