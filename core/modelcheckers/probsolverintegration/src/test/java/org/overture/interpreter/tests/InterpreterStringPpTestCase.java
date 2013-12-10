package org.overture.interpreter.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Result;

public class InterpreterStringPpTestCase extends InterpreterStringBaseTestCase
{
	public InterpreterStringPpTestCase()
	{
		super();

	}

	public InterpreterStringPpTestCase(File file)
	{
		super(file);
	}

	public InterpreterStringPpTestCase(File rootSource, String name,
			String content)
	{
		super(rootSource, name, content);
	}

	public InterpreterStringPpTestCase(File file, String suiteName,
			File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Override
	public void test() throws Exception
	{
		Result<String> result = null;
		if (mode == ContentModed.File)
		{
			@SuppressWarnings("rawtypes")
			TypeCheckResult tcResult = typeCheck();
			if (!tcResult.parserResult.errors.isEmpty()
					|| !tcResult.errors.isEmpty())
			{
				return;
				// fail("Model did not pass type check!."+ tcResult.errors);
			}
			String entry = "1+1";
			if (getEntryFile() == null || !getEntryFile().exists())
			{
				entry = createEntryFile();
				if (entry == null || getEntryFile() == null
						|| !getEntryFile().exists())
				{
					fail("No entry for model (" + getEntryFile() + ")");
				}
			} else
			{
				entry = getEntries().get(0);
			}
			try
			{
				Value val = InterpreterUtil.interpret(Settings.dialect, entry, file);
				result = new Result<String>(val.toString(), new Vector<IMessage>(), new Vector<IMessage>());
				System.out.println(file.getName() + " -> " + val);
			} catch (Exception e)
			{
				result = ExecutionToResultTranslator.wrap(e);
			}
			compareResults(result, file.getName() + ".result");
		}

	}

	@SuppressWarnings("rawtypes")
	protected TypeCheckResult typeCheck()
	{
		return TypeCheckerUtil.typeCheckPp(file);
	}

	protected String baseExamplePath()
	{
		return "C:\\overture\\overture_gitAST\\documentation\\examples\\VDMSL";
	}

	private String createEntryFile()
	{
		try
		{
			String tmp = search(new File(baseExamplePath()), file.getName());

			if (tmp != null && !tmp.isEmpty())
			{
				createResultFile(file.getName() + ".entry");
				FileWriter fstream = new FileWriter(getEntryFile());
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(tmp);
				out.close();
				return tmp;
			}
		} catch (IOException e)
		{
		}
		return null;

	}

	protected String search(File file, String name) throws IOException
	{
		File readme = new File(new File(file, name.substring(0, name.length() - 2)), "README.txt");
		if (readme.exists())
		{
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new FileReader(readme));

				String text = null;
				while ((text = reader.readLine()) != null)
				{
					text = text.trim();
					if (text.startsWith("#ENTRY_POINT"))
					{
						return text.substring(text.indexOf('=') + 1).trim();
					}
				}
			} finally
			{
				reader.close();
			}
		}
		return null;
	}

	private File getEntryFile()
	{
		return getResultFile(file.getName() + ".entry");
	}

	private List<String> getEntries() throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(getEntryFile()));
		List<String> data = new Vector<String>();
		String text = null;
		while ((text = reader.readLine()) != null)
		{
			data.add(text.trim());
		}
		reader.close();

		return data;
	}

}
