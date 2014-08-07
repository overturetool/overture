package org.overture.core.tests.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;

/**
 * Intermediary example information. The packer takes a single example stored in disk and processes its Readme file.
 * This class can then provide information regarding the example that is necessary when building its AST (Dialect, Libs,
 * etc.). <br>
 * <br>
 * This class is <b>not</b> for use outside the new test framework.
 * 
 * @author ldc
 */
class ExamplePacker
{
	/**
	 * The various results that can be declated in the readme.
	 * 
	 * @author ldc
	 */
	private enum ResultStatus
	{
		NO_ERROR_SYNTAX, NO_ERROR_TYPE_CHECK, NO_CHECK, NO_ERROR_PO, NO_ERROR_INTERPRETER
	}

	/**
	 * Return the "name" of a dialect. Used to construct the example name and to find its parent folder in the examples
	 * folder (examples are grouped by dialect).
	 * 
	 * @param dialect
	 * @return
	 */
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

	// Readme entries
	private final String EXPECTED_RESULT = "EXPECTED_RESULT";
	private final String LANGUAGE_VERSION = "LANGUAGE_VERSION";
	private final String LIB = "LIB";

	Dialect dialect;
	Release languageVersion;
	Boolean checkable;
	String name;
	File root;

	private List<String> libs = new Vector<String>();

	public ExamplePacker(File root, Dialect dialect)
	{
		this.dialect = dialect;
		this.root = root;
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

	/**
	 * Process the example readme file and initialize the packer. We disregard most of the readme entries since we only
	 * care about a handful of thins.
	 * 
	 * @param file
	 */
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

	private void processExpectedResult(ResultStatus expectedResult)
	{// no check means there are parse/type error
		if (expectedResult.equals(ResultStatus.NO_CHECK))
		{
			checkable = false;
		} else
		{
			checkable = true;
		}
	}

	/**
	 * Process the lib dependencies (IO, MATH, etc.) of this example.
	 * 
	 * @param text
	 */
	private void processLibs(String text)
	{
		// separate libs in the README entry
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

		// now add the libs
		for (String lib : libs)
		{
			if (lib.trim().length() > 0)
			{
				this.libs.add(lib.trim() + "." + getSpecFileExtension());
			}
		}

	}

	/**
	 * Process a single line in the Readme file. Again, most entries are ignored.
	 * 
	 * @param line
	 */
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

	public List<File> getSpecFiles()
	{
		List<File> files = new Vector<File>();

		for (File f2 : org.apache.commons.io.FileUtils.listFiles(this.root, new AbstractFileFilter()
		{


			@Override
			public boolean accept(File dir, String name)
			{
				return dialect.getFilter().accept(dir, name);
			}

		}, TrueFileFilter.INSTANCE))
		{
			files.add(f2);
		}
		
		return files;
	}

}
