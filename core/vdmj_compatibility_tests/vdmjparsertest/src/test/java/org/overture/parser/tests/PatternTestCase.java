package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.PatternReader;

public class PatternTestCase extends BaseParserTestCase<PatternReader>
{
	static boolean hasRunBefore = false;
	public PatternTestCase(File file)
	{
		super(file);
	}
	
	public PatternTestCase(String name, String content)
	{
		super(name, content);
	}

	@Override
	protected PatternReader getReader(LexTokenReader ltr)
	{
	return new PatternReader(ltr);
	}

	@Override
	protected List<Pattern> read(PatternReader reader) throws ParserException,
			LexException
	{
		return reader.readPatternList();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Pattern";
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
