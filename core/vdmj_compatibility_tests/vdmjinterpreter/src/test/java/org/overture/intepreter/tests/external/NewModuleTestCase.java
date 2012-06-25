package org.overture.intepreter.tests.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.overture.interpreter.tests.OvertureTestHelper;
import org.overture.interpreter.tests.framework.ModuleTestCase;
import org.overturetool.test.framework.TestResourcesResultTestCase;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NewModuleTestCase extends ModuleTestCase
{
	public NewModuleTestCase()
	{
		super();
	}

	public NewModuleTestCase(File file)
	{
		super(file);
	}

	public NewModuleTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	public NewModuleTestCase(File file, String suiteName, File testSuiteRoot)
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

	@Override
	protected String baseExamplePath()
	{
	return file.getParentFile().getAbsolutePath();
	}


	protected String search(File file, String name) throws IOException
	{
		File readme = new File(file,(name.contains(".")?name.substring(0,name.lastIndexOf('.'))+".assert": name+".assert"));
		if (readme.exists())
		{
			BufferedReader reader = new BufferedReader(new FileReader(readme));
			String text = null;
			String entry ="";
			while ((text = reader.readLine()) != null)
			{
				entry += text;
			}
			entry = entry.replace('\n', ' ');
			reader.close();
			return entry;
		}
		return null;
	}
}
