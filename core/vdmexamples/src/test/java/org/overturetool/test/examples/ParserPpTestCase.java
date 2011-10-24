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
import org.overturetool.vdmj.syntax.ClassReader;

public class ParserPpTestCase extends ExamplesTestCase
{
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
		if(mode==ContentModed.None)
		{
			return;
		}
		
		ParserProxy<ClassReader, List<ClassDefinition>> parser = new ParserProxy<ClassReader, List<ClassDefinition>>(VdmjFactories.vdmPpParserfactory, getSpecFiles("vdmpp", file));
		Result<List<ClassDefinition>> res = mergeResults(parser.parse(),new IResultCombiner<List<ClassDefinition>>()
		{

			public List<ClassDefinition> combine(List<ClassDefinition> a,
					List<ClassDefinition> b)
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
		Settings.dialect = Dialect.VDM_PP;
	}
}
