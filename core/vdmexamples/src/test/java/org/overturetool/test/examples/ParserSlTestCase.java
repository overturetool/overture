package org.overturetool.test.examples;

import java.io.File;
import java.util.List;

import org.overturetool.test.examples.vdmj.ParserProxy;
import org.overturetool.test.examples.vdmj.VdmjFactories;
import org.overturetool.test.framework.examples.ExamplesTestCase;
import org.overturetool.test.framework.examples.IResultCombiner;
import org.overturetool.test.framework.examples.Result;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.syntax.ClassReader;
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
		if(mode==ContentModed.None)
		{
			return;
		}
		
		ParserProxy<ModuleReader, List<Module>> parser = new ParserProxy<ModuleReader, List<Module>>(VdmjFactories.vdmSlParserfactory, getSpecFiles("vdmpp", file));
		Result<List<Module>> res = mergeResults(parser.parse(),new IResultCombiner<List<Module>>()
				{

					public List<Module> combine(List<Module> a,
							List<Module> b)
					{
						return null;
					}
				});
				compareResults(res.warnings,res.errors,res.result);
	}



	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
	}
}
