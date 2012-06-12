package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.patterns.PPattern;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.PatternReader;
import org.overture.parser.tests.framework.BaseParserTestCase;

public class PatternTestCase extends BaseParserTestCase<PatternReader,List<PPattern>>
{
	static boolean hasRunBefore = false;

	public PatternTestCase(File file)
	{
		super(file);
	}

	public PatternTestCase()
	{

	}

	public PatternTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	@Override
	protected PatternReader getReader(LexTokenReader ltr)
	{
		return new PatternReader(ltr);
	}

	@Override
	protected List<PPattern> read(PatternReader reader) throws ParserException,
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
