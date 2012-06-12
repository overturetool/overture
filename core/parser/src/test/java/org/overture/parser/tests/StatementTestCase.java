package org.overture.parser.tests;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.ast.statements.PStm;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.StatementReader;
import org.overture.parser.tests.framework.BaseParserTestCase;

public class StatementTestCase extends BaseParserTestCase<StatementReader,PStm>
{
	static boolean hasRunBefore = false;
	public StatementTestCase(File file)
	{
		super(file);
	}
	
	public StatementTestCase()
	{
	
	}

	public StatementTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
	}

	@Override
	protected StatementReader getReader(LexTokenReader ltr)
	{
		return new StatementReader(ltr);
	}

	@Override
	protected PStm read(StatementReader reader) throws ParserException, LexException
	{
		return reader.readStatement();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Statement";
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
