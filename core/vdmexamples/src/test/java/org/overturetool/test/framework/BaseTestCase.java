package org.overturetool.test.framework;

import java.io.File;

import junit.framework.TestCase;

public abstract class BaseTestCase extends
		TestCase
{
	protected File file;
	protected String name;
	protected String content;
	protected enum ContentModed {File, String, None};
	protected final ContentModed mode;
	

	public BaseTestCase()
	{
		super("skip");
		mode = ContentModed.None;
	}
	

	public BaseTestCase(File file)
	{
		super("test");
		this.file = file;
		this.content = file.getName();
		mode = ContentModed.File;
	}

	public BaseTestCase(String name, String content)
	{
		super("test");
		this.content = content;
		this.name = name;
		mode = ContentModed.String;
	}

	@Override
	public String getName()
	{
		if (name != null)
		{
			return name;
		} else if (file != null)
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

	public abstract void test() throws Exception;
	
	
	public static String pad(String text, int length)
	{
		if (text == null)
		{
			text = "null";
		}
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}
	
	public void skip(){};
}
