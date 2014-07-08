package org.overture.core.tests.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;

public class ExamplePacker
{
	private enum ResultStatus
	{
		NO_ERROR_SYNTAX, NO_ERROR_TYPE_CHECK, NO_CHECK, NO_ERROR_PO, NO_ERROR_INTERPRETER
	}

	public static String getName(Dialect dialect)
	{
		switch (dialect)
		{
			case VDM_PP:
				return "PP";
			case VDM_RT:
				return "RT";
			case VDM_SL:
				return "SL";
			default:
				return "PP";
		}
	}

	public static final String VDM_README_FILENAME = "README.txt";

	private final String EXPECTED_RESULT = "EXPECTED_RESULT";
	private final String LANGUAGE_VERSION = "LANGUAGE_VERSION";
	private final String LIB = "LIB";

	Dialect dialect;
	Release languageVersion;
	Boolean checkable;
	String name;

	private List<String> libs = new Vector<String>();

	public ExamplePacker(File root, Dialect dialect)
	{
		this.dialect = dialect;
		name = root.getName() + getName(dialect);

		File readme = new File(root, VDM_README_FILENAME);

		initialize(readme);
	}

	public Release getLanguageVersion()
	{
		return languageVersion;
	}

	public List<String> getLibs()
	{
		return libs;
	}

	public String getName()
	{
		return name;
	}

	private String getSpecFileExtension()
	{
		switch (dialect)
		{
			case VDM_PP:
				return "vdmpp";
			case VDM_RT:
				return "vdmrt";
			case VDM_SL:
				return "vdmsl";

			default:
				return "vdmpp";
		}
	}

	public void initialize(File file)
	{
		try
		{
			// readme parser. code copied over from example packager
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			try
			{
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = input.readLine()) != null)
				{
					if (line.startsWith("#") && line.contains("="))
					{
						processLine(line.substring(1).trim());
					}
					if (!line.startsWith("#"))
					{
						sb.append("\n" + line);
					}
				}
			} finally
			{
				input.close();
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}

	}

	public boolean isCheckable()
	{
		return checkable;
	}

	private void processExpectedResult(ResultStatus expectedResult)
	{
		if (expectedResult.equals(ResultStatus.NO_CHECK))
		{
			checkable = false;
		} else
		{
			checkable = true;
		}
	}

	private void processLine(String line)
	{
		String[] data = line.split("=");
		if (data.length > 1)
		{
			if (data[0].equals(LANGUAGE_VERSION))
			{
				languageVersion = Release.lookup(data[1]);
			} else if (data[0].equals(LIB))
			{
				processLibs(data[1]);
			} else if (data[0].equals(EXPECTED_RESULT))
			{
				processExpectedResult(ResultStatus.valueOf(data[1]));
			}
		}
	}

	private void processLibs(String text)
	{
		// fix splits
		String[] libs;
		String splitter = ",";
		if (text.contains(splitter))
		{
			libs = text.trim().split(splitter);
		} else
		{
			splitter = ";";
			if (text.contains(splitter))
			{
				libs = text.trim().split(splitter);
			} else
			{
				libs = new String[] { text };
			}
		}

		// add libs
		for (String lib : libs)
		{
			if (lib.trim().length() > 0)
			{
				this.libs.add(lib.trim() + "." + getSpecFileExtension());
			}
		}

	}

}
