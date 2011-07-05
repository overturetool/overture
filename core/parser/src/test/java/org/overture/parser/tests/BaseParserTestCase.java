package org.overture.parser.tests;

import java.io.File;

import junit.framework.TestCase;

import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;

public abstract class BaseParserTestCase extends TestCase
{
	File file;

	public BaseParserTestCase()
	{
		super("test");
	}

	public BaseParserTestCase(File file)
	{
		super("test");
		this.file = file;
	}

	@Override
	public String getName()
	{
		if (file != null)
		{
			String name = file.getName();
			if (name.contains("."))
			{
				return name.substring(0, name.indexOf("."));
			}
			return file.getName();
		}
		return "Generic Base Test";
	}

	public void test() throws ParserException, LexException
	{
		if (file != null)
		{
			internal(file);
		}
	}

	public abstract void internal(File file) throws ParserException, LexException;
}
