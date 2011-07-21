package org.overture.typechecker.tests.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import org.overture.typechecker.tests.framework.BasicTypeCheckTestCase.ParserType;

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
	public void test(){}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static TestSuite createTestCompleteFile(String name,
			String testRootPath, Class testCase)
			throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException
	{
		File testRoot = getFile(testRootPath);
		Constructor ctor = testCase.getConstructor(new Class[] { File.class });
		TestSuite suite = new BaseTestSuite(name);

		if (testRoot != null && testRoot.exists())
		{

			for (File file : testRoot.listFiles())
			{
				createCompleteFile(suite, file, ctor);
			}
		}
		return suite;

	}

	private static void createCompleteFile(TestSuite suite, File file,
			@SuppressWarnings("rawtypes") Constructor ctor)
			throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException
	{
		if (file.getName().startsWith("."))
		{
			return;
		}
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				createCompleteFile(suite, f, ctor);
			}
		} else
		{
			System.out.println("Creating test for:" + file);
			Object instance = ctor.newInstance(new Object[] { file });
			suite.addTest((Test) instance);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static TestSuite createTestSingleLineFile(ParserType type,
			String name, String testRootPath, Class testCase)
			throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException, IOException
	{
		File testRoot = getFile(testRootPath);
		Constructor ctor = testCase.getConstructor(new Class[] {ParserType.class, String.class,File.class,String.class,
				String.class });
		TestSuite suite = new BaseTestSuite(name);

		if (testRoot != null && testRoot.exists())
		{
			for (File file : testRoot.listFiles())
			{
				if (file.getName().startsWith(".")|| file.getName().endsWith("_generated"))
				{
					continue;
				}
				List<String> lines = readFile(file);
				if (lines != null)
				{
					for (int i = 0; i < lines.size(); i++)
					{
						Object instance = ctor.newInstance(new Object[] {
								type,
								file.getName() + " " + i + " - " + splitContentResult(lines.get(i))[0],
								file,
								splitContentResult(lines.get(i))[0],
								splitContentResult(lines.get(i))[1]});
						suite.addTest((Test) instance);
					}
				}

			}
		}
		return suite;

	}

	private static String[] splitContentResult(String line)
	{
		String[] tmp = new String[]{"",""};
		if(line.indexOf('$')!=-1)
		{
			tmp[0]=line.substring(line.indexOf('$')+1).trim();
			tmp[1]=line.substring(0,line.indexOf('$')).trim();
		}
		else
		{
			tmp[0]= line.trim();
		}
		return tmp;
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
		return new File(pathname.replace('\\', File.separatorChar).replace('/', File.separatorChar));
	}
}
