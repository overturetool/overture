package org.overture.interpreter.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Result;

public class InterpreterSlTestCase extends InterpreterBaseTestCase
{
	public InterpreterSlTestCase()
	{
		super();

	}

	public InterpreterSlTestCase(File file)
	{
		super(file);
	}

	public InterpreterSlTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.CLASSIC;
	}

	@Override
	public void test() throws Exception
	{
		Result<Value> result = null;
		if (mode == ContentModed.File)
		{
			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(file);
			if (!tcResult.parserResult.errors.isEmpty()
					|| !tcResult.errors.isEmpty())
			{
				fail("Model did not pass type check!.");
			}
			String entry = "1+1";
			if(getEntryFile()==null || !getEntryFile().exists())
			{
//				fail("No entry for model ("+getEntryFile()+")");
			}else
			{
				entry = getEntries().get(0);
			}
			Value val = InterpreterUtil.interpret(entry,file);
			result = new Result<Value>(val, new Vector<IMessage>(), new Vector<IMessage>());
			compareResults(result, file.getAbsolutePath());
		}
		
	}
	
	private File getEntryFile()
	{
		return new File(file.getParentFile(),file.getName()+".entry");
	}

	private List<String> getEntries() throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(getEntryFile()));
		List<String> data = new Vector<String>();
		String text = null;
		while ((text = reader.readLine()) != null)
		{
			data.add( text.trim());
		}
		reader.close();

		return data;
	}

}
