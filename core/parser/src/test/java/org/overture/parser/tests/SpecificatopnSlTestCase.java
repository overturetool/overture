package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ModuleReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.tests.framework.BaseParserTestCase;

public class SpecificatopnSlTestCase extends BaseParserTestCase<ModuleReader,List<AModuleModules>>
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

	public SpecificatopnSlTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
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
