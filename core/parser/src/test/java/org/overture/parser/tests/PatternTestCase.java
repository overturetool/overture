package org.overture.parser.tests;

import java.io.File;
import java.io.PrintWriter;

import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.PatternReader;

public class PatternTestCase extends BaseParserTestCase
{
	public PatternTestCase(File file)
	{
		super(file);
	}
	
	public PatternTestCase(String name, String content)
	{
		super(name, content);
	}


	@Override
	public void internal(File file) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_SL;
		PatternReader reader = null;

		LexTokenReader ltr = new LexTokenReader(file, Settings.dialect);
		reader = new PatternReader(ltr);
		PPattern expression = reader.readPattern();

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

		System.out.println("Parsed pattern: " + expression);

	}

	@Override
	public void internal(String content) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_SL;
		PatternReader reader = null;

		LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
		reader = new PatternReader(ltr);
		PPattern expression = reader.readPattern();

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

		System.out.println("Parsed pattern: " + expression);
	}
}
