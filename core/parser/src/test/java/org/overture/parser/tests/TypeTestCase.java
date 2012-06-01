package org.overture.parser.tests;

import java.io.File;

import org.overture.ast.node.INode;
import org.overture.parser.tests.framework.BaseParserTestCase;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.TypeReader;

public class TypeTestCase extends BaseParserTestCase<TypeReader,INode>
{
	static boolean hasRunBefore = false;

	public TypeTestCase(File file)
	{
		super(file);
	}

	public TypeTestCase()
	{

	}

	public TypeTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
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
