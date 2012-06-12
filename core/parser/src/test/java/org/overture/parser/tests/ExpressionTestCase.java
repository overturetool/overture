package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.expressions.PExp;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.tests.framework.BaseParserTestCase;

public class ExpressionTestCase extends BaseParserTestCase<ExpressionReader,List<PExp>>
{
	static boolean hasRunBefore = false;

	public ExpressionTestCase(File file)
	{
		super(file);
	}

	public ExpressionTestCase()
	{
	}

	public ExpressionTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	@Override
	protected ExpressionReader getReader(LexTokenReader ltr)
	{
		return new ExpressionReader(ltr);
	}

	@Override
	protected List<PExp> read(ExpressionReader reader) throws ParserException,
			LexException
	{
		return reader.readExpressionList();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Expression";
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
