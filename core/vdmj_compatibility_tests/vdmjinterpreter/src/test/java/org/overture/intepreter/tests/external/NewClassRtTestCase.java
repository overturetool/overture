package org.overture.intepreter.tests.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.overture.interpreter.tests.framework.ClassTestCase;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class NewClassRtTestCase extends ClassTestCase
{
	public NewClassRtTestCase()
	{
		super();
	}

	public NewClassRtTestCase(File file)
	{
		super(file);
	}

	public NewClassRtTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	public NewClassRtTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_RT;
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
