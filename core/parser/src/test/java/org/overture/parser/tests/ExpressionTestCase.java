package org.overture.parser.tests;

import java.io.File;
import java.io.PrintWriter;

import org.overture.ast.expressions.PExp;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.ParserException;

public class ExpressionTestCase extends BaseParserTestCase
{
	public ExpressionTestCase(File file)
	{
		super(file);
	}

	public ExpressionTestCase(String name, String content)
	{
		super(name, content);
	}

	@Override
	public void internal(File file) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
		ExpressionReader reader = null;

		LexTokenReader ltr = new LexTokenReader(file, Settings.dialect);
		reader = new ExpressionReader(ltr);
		PExp expression = (reader.readExpression());

		if (reader != null && reader.getErrorCount() > 0)
		{
			// perrs += reader.getErrorCount();
			reader.printErrors(new PrintWriter(System.out));

		}
		assertEquals(reader.getErrorCount(), 0);

		if (reader != null && reader.getWarningCount() > 0)
		{
			// pwarn += reader.getWarningCount();
			reader.printWarnings(new PrintWriter(System.out));
		}

		System.out.println("Parsed Expression: " + expression);

	}

	@Override
	public void internal(String content) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
		ExpressionReader reader = null;
		PExp expression = null;
		try
		{
			LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
			reader = new ExpressionReader(ltr);
			expression = (reader.readExpression());

			if (reader != null && reader.getErrorCount() > 0)
			{
				// perrs += reader.getErrorCount();
				reader.printErrors(new PrintWriter(System.out));

			}
			assertEquals(reader.getErrorCount(), 0);

			if (reader != null && reader.getWarningCount() > 0)
			{
				// pwarn += reader.getWarningCount();
				reader.printWarnings(new PrintWriter(System.out));
			}
		} finally
		{
			System.out.println("Parsed Expression: \"" + content + "\" as: "
					+ expression);
		}
	}
}
