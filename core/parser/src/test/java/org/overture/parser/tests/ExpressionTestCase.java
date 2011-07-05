package org.overture.parser.tests;

import java.io.File;
import java.io.PrintWriter;

import org.overture.ast.expressions.PExp;
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

	@Override
	public void internal(File file) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_SL;
		ExpressionReader reader = null;

		LexTokenReader ltr = new LexTokenReader(file, Settings.dialect);
		reader = new ExpressionReader(ltr);
		PExp expression = (reader.readExpression());

		if (reader != null && reader.getErrorCount() > 0)
		{
			// perrs += reader.getErrorCount();
			reader.printErrors(new PrintWriter(System.out));
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			// pwarn += reader.getWarningCount();
			reader.printWarnings(new PrintWriter(System.out));
		}

		System.out.println("Parsed: " + expression);

	}
}
