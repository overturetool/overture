package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.modules.AModuleModules;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;

public class SpecificatopnSlTestCase extends BaseParserTestCase<ModuleReader>
{
	static boolean hasRunBefore = false;
	public SpecificatopnSlTestCase(File file)
	{
		super(file);
	}
	
	public SpecificatopnSlTestCase()
	{
	
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
	}

	public SpecificatopnSlTestCase(String name, String content)
	{
		super(name, content);
	}

	@Override
	protected ModuleReader getReader(LexTokenReader ltr)
	{
		return new ModuleReader(ltr);
	}

	@Override
	protected List<AModuleModules> read(ModuleReader reader) throws ParserException, LexException
	{
		return reader.readModules();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Specificatopn SL";
	}

	@Override
	protected void setHasRunBefore(boolean b)
	{
		hasRunBefore = b;
	}

	@Override
	protected boolean hasRunBefore()
	{
		return hasRunBefore;
	}

	
}
