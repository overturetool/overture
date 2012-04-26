package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmjV2.Settings;
import org.overturetool.vdmjV2.lex.Dialect;
import org.overturetool.vdmjV2.lex.LexException;
import org.overturetool.vdmjV2.lex.LexTokenReader;
import org.overturetool.vdmjV2.syntax.ClassReader;
import org.overturetool.vdmjV2.syntax.ParserException;

public class SpecificatopnPpTestCase extends BaseParserTestCase<ClassReader>
{
	static boolean hasRunBefore = false;
	public SpecificatopnPpTestCase(File file)
	{
		super(file);
	}
	
	public SpecificatopnPpTestCase()
	{
	
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
	}

	public SpecificatopnPpTestCase(String name, String content)
	{
		super(name, content);
	}

	@Override
	protected ClassReader getReader(LexTokenReader ltr)
	{
		return new ClassReader(ltr);
	}

	@Override
	protected List<SClassDefinition> read(ClassReader reader) throws ParserException, LexException
	{
		return reader.readClasses();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Specificatopn PP";
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
