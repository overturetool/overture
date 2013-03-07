package org.overture.interpreter.tests.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.interpreter.tests.OvertureTestHelper;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.values.Value;

public class ModuleTestCase extends InterpreterBaseTestCase
{

	public static final String tcHeader = "-- TCErrors:";

	String name;
	String content;
	String expectedType;

	public ModuleTestCase()
	{
		super();
	}

	public ModuleTestCase(File file)
	{
		super(file);
		this.content = file.getName();
	}

	public ModuleTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	public ModuleTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException
	{
		Result<String> result = null;
		if (mode == ContentModed.File)
		{
			String entry = "1 + 1";
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
			Value val;
			try
			{
				val = new OvertureTestHelper().interpret(Settings.dialect, entry, file);
				result = new Result<String>(val.toString(), new Vector<IMessage>(), new Vector<IMessage>());
				System.out.println(file.getName() + " -> " + val);

			} catch (Exception e)
			{
				result = new Result<String>(e.getMessage(), new Vector<IMessage>(), new Vector<IMessage>());
			}

			compareResults(result, file.getName() + ".result");
		}
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

}
