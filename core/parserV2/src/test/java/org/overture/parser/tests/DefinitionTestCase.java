package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmjV2.Settings;
import org.overturetool.vdmjV2.lex.Dialect;
import org.overturetool.vdmjV2.lex.LexException;
import org.overturetool.vdmjV2.lex.LexTokenReader;
import org.overturetool.vdmjV2.syntax.DefinitionReader;
import org.overturetool.vdmjV2.syntax.ParserException;

public class DefinitionTestCase extends BaseParserTestCase<DefinitionReader>
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

	public DefinitionTestCase(String name, String content)
	{
		super(name, content);
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
