package org.overture.parser.tests.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BaseTestSuite extends TestSuite
{
	public BaseTestSuite(String name)
	{
		super(name);
	}

	public BaseTestSuite()
	{

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static TestSuite createTestCompleteFile(String name,
			String testRootPath, Class testCase) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException
	{
		File testRoot = getFile(testRootPath);
		Constructor ctor = testCase.getConstructor(new Class[] { File.class });
		TestSuite suite = new BaseTestSuite(name);

		if (testRoot != null && testRoot.exists())
		{

			for (File file : testRoot.listFiles())
			{
				createCompleteFile(suite,file,ctor);
			}
		}
		return suite;

	}
	
	
	private static void createCompleteFile(TestSuite suite, File file, @SuppressWarnings("rawtypes") Constructor ctor) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		if (file.getName().startsWith(".") || file.getName().endsWith(".result"))
		{
			return;
		}
		if(file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				createCompleteFile(suite,f,ctor);
			}
		}else
		{
			System.out.println("Creating test for:"+ file);
			Object instance = ctor.newInstance(new Object[] { file });
			suite.addTest((Test) instance);
		}
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static TestSuite createTestSingleLineFile(String name,
			String testRootPath, Class testCase) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException,
			NoSuchMethodException, IOException
	{
		File testRoot = getFile(testRootPath);
		Constructor ctor = testCase.getConstructor(new Class[] { String.class,
				String.class });
		TestSuite suite = new BaseTestSuite(name);

		if (testRoot != null && testRoot.exists())
		{
			for (File file : testRoot.listFiles())
			{
				if (file.getName().startsWith("."))
				{
					continue;
				}
				List<String> lines = readFile(file);
				if (lines != null)
				{
					for (int i = 0; i < lines.size(); i++)
					{
						Object instance = ctor.newInstance(new Object[] {
								file.getName() + " " + i + " - " + lines.get(i),
								lines.get(i) });
						suite.addTest((Test) instance);
					}
				}

			}
		}
		return suite;

	}

	protected static List<String> readFile(File file) throws IOException
	{
		List<String> lines = new Vector<String>();
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			// repeat until all lines is read
			while ((text = reader.readLine()) != null)
			{
				if (text.trim().length() > 0 && !text.trim().startsWith("//"))
				{
					lines.add(text);
				}
			}
			return lines;
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
			}
		}
	}
	
	protected static File getFile(String pathname)
	{
		pathname ="..\\vdmjparsertest\\"+pathname;
		return new File(pathname.replace('\\', File.separatorChar).replace('/', File.separatorChar));
	}
}
