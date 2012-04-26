package org.overture.parser.tests;

import java.io.File;

import org.overture.ast.statements.PStm;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmjV2.Settings;
import org.overturetool.vdmjV2.lex.Dialect;
import org.overturetool.vdmjV2.lex.LexException;
import org.overturetool.vdmjV2.lex.LexTokenReader;
import org.overturetool.vdmjV2.syntax.ParserException;
import org.overturetool.vdmjV2.syntax.StatementReader;

public class StatementTestCase extends BaseParserTestCase<StatementReader>
{
	static boolean hasRunBefore = false;
	public StatementTestCase(File file)
	{
		super(file);
	}
	
	public StatementTestCase()
	{
	
	}

	public StatementTestCase(String name, String content)
	{
		super(name, content);
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
