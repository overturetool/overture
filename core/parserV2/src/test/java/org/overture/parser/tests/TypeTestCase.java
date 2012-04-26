package org.overture.parser.tests;

import java.io.File;

import org.overture.ast.node.INode;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmjV2.lex.LexException;
import org.overturetool.vdmjV2.lex.LexTokenReader;
import org.overturetool.vdmjV2.syntax.ParserException;
import org.overturetool.vdmjV2.syntax.TypeReader;

public class TypeTestCase extends BaseParserTestCase<TypeReader>
{
	static boolean hasRunBefore = false;

	public TypeTestCase(File file)
	{
		super(file);
	}

	public TypeTestCase()
	{

	}

	public TypeTestCase(String name, String content)
	{
		super(name, content);
	}

	@Override
	protected TypeReader getReader(LexTokenReader ltr)
	{
		return new TypeReader(ltr);
	}

	@Override
	protected INode read(TypeReader reader) throws ParserException, LexException
	{
		return reader.readType();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Type";
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
