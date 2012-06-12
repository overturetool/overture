package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.tests.framework.BaseParserTestCase;

public class DefinitionTestCase extends BaseParserTestCase<DefinitionReader,List<PDefinition>>
{
	static boolean hasRunBefore = false;
	public DefinitionTestCase(File file)
	{
		super(file);
	}
	
	public DefinitionTestCase()
	{
	
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_RT;
	}

	public DefinitionTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	@Override
	protected DefinitionReader getReader(LexTokenReader ltr)
	{
		return new DefinitionReader(ltr);
	}

	@Override
	protected List<PDefinition> read(DefinitionReader reader) throws ParserException, LexException
	{
		return reader.readDefinitions();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Definition";
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
