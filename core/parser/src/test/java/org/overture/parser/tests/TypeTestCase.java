package org.overture.parser.tests;

import java.io.File;
import java.io.PrintWriter;

import org.overture.ast.types.PType;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.TypeReader;

public class TypeTestCase extends BaseParserTestCase
{
	public TypeTestCase(File file)
	{
		super(file);
	}
	
	public TypeTestCase(String name, String content)
	{
		super(name, content);
	}

	@Override
	public void internal(File file) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_SL;
		TypeReader reader = null;

		LexTokenReader ltr = new LexTokenReader(file, Settings.dialect);
		reader = new TypeReader(ltr);
		PType expression = reader.readType();

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

		System.out.println("Parsed: " + expression);

	}

	@Override
	public void internal(String content) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_SL;
		TypeReader reader = null;

		LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
		reader = new TypeReader(ltr);
		PType expression = reader.readType();

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

		System.out.println("Parsed: " + expression);
	}
}
