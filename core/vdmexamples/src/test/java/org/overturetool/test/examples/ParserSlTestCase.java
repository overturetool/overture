package org.overturetool.test.examples;

import java.io.File;
import java.util.List;

import org.overturetool.test.examples.vdmj.ParserProxy;
import org.overturetool.test.examples.vdmj.VdmjFactories;
import org.overturetool.test.framework.examples.ExamplesTestCase;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.syntax.ClassReader;

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
		
		ParserProxy<ClassReader, List<ClassDefinition>> parser = new ParserProxy<ClassReader, List<ClassDefinition>>(VdmjFactories.vdmParserfactory, getSpecFiles("vdmpp", file));
		compareResults(parser.parse());
	}



	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
	}
}
