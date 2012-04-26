package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.expressions.PExp;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmjV2.lex.LexException;
import org.overturetool.vdmjV2.lex.LexTokenReader;
import org.overturetool.vdmjV2.syntax.ExpressionReader;
import org.overturetool.vdmjV2.syntax.ParserException;

public class ExpressionTestCase extends BaseParserTestCase<ExpressionReader>
{
	static boolean hasRunBefore = false;

	public ExpressionTestCase(File file)
	{
		super(file);
	}

	public ExpressionTestCase()
	{
	}

	public ExpressionTestCase(String name, String content)
	{
		super(name, content);
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
