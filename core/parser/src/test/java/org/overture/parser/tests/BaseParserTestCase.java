package org.overture.parser.tests;

import java.io.File;

import junit.framework.TestCase;

import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;

public abstract class BaseParserTestCase extends TestCase
{
	File file;
	String name ;
	String content;

	public BaseParserTestCase()
	{
		super("test");
	}

	public BaseParserTestCase(File file)
	{
		super("test");
		this.file = file;
	}
	
	public  BaseParserTestCase(String name, String content)
	{
		super("test");
		this.content = content;
		this.name  =name;
	}

	@Override
	public String getName()
	{
		if(name !=null)
		{
			return name;
		}else if (file != null)
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
		}else if(content != null)
		{
			internal(content);
		}
	}

	public abstract void internal(String content) throws ParserException, LexException;

	public abstract void internal(File file) throws ParserException, LexException;
}
