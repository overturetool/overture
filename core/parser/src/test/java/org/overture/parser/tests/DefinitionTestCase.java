package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.DefinitionReader;
import org.overturetool.vdmj.syntax.ParserException;

public class DefinitionTestCase extends BaseParserTestCase<DefinitionReader>
{
	static boolean hasRunBefore = false;
	public DefinitionTestCase(File file)
	{
		super(file);
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
