package org.overture.prettyprintertest.tests;


import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.util.definitions.ClassList;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overture.prettyprinter.PrettyPrinterEnv;
import org.overture.prettyprinter.PrettyPrinterVisitor;
import org.overture.prettyprintertest.tests.framework.BasePrettyPrinterTestCase;

public class SpecificationPpTestCase extends BasePrettyPrinterTestCase<ClassReader,List<String>>
{
	static boolean hasRunBefore = false;
	public SpecificationPpTestCase(File file)
	{
		super(file);
	}
		
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
	}

	public SpecificationPpTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	@Override
	protected ClassReader getReader(LexTokenReader ltr)
	{
		return new ClassReader(ltr);
	}

	@Override
	protected List<String> read(ClassReader reader) throws ParserException, LexException
	{
		ClassList classes = reader.readClasses();
		List<String> result = new Vector<String>();
		for (SClassDefinition sClassDefinition : classes)
		{
			try
			{
				result.add(sClassDefinition.apply(new PrettyPrinterVisitor(), new PrettyPrinterEnv()));
			} catch (AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (String string : result)
		{
			System.out.println(string);
		}
		return result;
	}

	

	

	
}
