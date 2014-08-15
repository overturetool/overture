/*
 * #%~
 * Integration of the ProB Solver for the VDM Interpreter
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.interpreter.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.test.framework.ConditionalIgnoreMethodRule;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Result;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public abstract class CommonInterpreterTest extends StringBasedInterpreterTest
{
	private Dialect dialect;

	public CommonInterpreterTest(Dialect dialect, File file, String suiteName,
			File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
		this.dialect = dialect;
	}

	@Before
	public void setUp() throws Exception
	{
		Settings.dialect = dialect;
		Settings.release = Release.VDM_10;
	}

	@Rule
	public ConditionalIgnoreMethodRule rule = new ConditionalIgnoreMethodRule();

	@Test
	@ConditionalIgnore(condition = ProbNotInstalledCondition.class)
	public void test() throws Exception
	{
		configureResultGeneration();
		try
		{
			Result<String> result = null;
			@SuppressWarnings("rawtypes")
			TypeCheckResult tcResult = typeCheck();
			if (!tcResult.parserResult.errors.isEmpty()
					|| !tcResult.errors.isEmpty())
			{
				// Assert.fail("Model did not pass type check!."+ tcResult.errors);
				Assume.assumeTrue("Specification does not type check: \n"
						+ tcResult.parserResult.errors + "\n\n"
						+ tcResult.errors, false);
				return;
				// fail("Model did not pass type check!."+ tcResult.errors);
			}
			String entry = "1+1";
			if (getEntryFile() == null || !getEntryFile().exists())
			{
				entry = createEntryFile();
				if (entry == null)
				{
					if (getEntryFile() == null || !getEntryFile().exists())
					{
						Assert.fail("No entry for model (" + getEntryFile()
								+ ")");
					}
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
		} finally
		{
			unconfigureResultGeneration();
		}
	}

	@SuppressWarnings("rawtypes")
	protected TypeCheckResult typeCheck() throws ParserException, LexException
	{
		if (dialect == Dialect.VDM_SL)
		{
			return TypeCheckerUtil.typeCheckSl(file);
		} else if (dialect == Dialect.VDM_PP)
		{

			return TypeCheckerUtil.typeCheckPp(file);
		} else if (dialect == Dialect.VDM_RT)
		{
			return TypeCheckerUtil.typeCheckRt(file);
		}
		return null;
	}

	protected File getStorageLocation()
	{
		return file.getParentFile();
	}

	protected File getInputLocation()
	{
		return file.getParentFile();
	}

	@Override
	protected File createResultFile(String filename)
	{
		getStorageLocation().mkdirs();
		return getResultFile(filename);
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(getStorageLocation(), filename);
	}

	private File getEntryFile()
	{
		return new File(getStorageLocation(), file.getName() + ".entry");
	}

	protected String createEntryFile()
	{
		try
		{
			String tmp = search(getInputLocation(), file.getName());

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
		// File readme = new File(new File(file, name.substring(0, name.length() - 2)), "README.txt");
		// if (readme.exists())
		// {
		// BufferedReader reader = null;
		// try
		// {
		// reader = new BufferedReader(new FileReader(readme));

		return "test()";
		// while ((text = reader.readLine()) != null)
		// {
		// text = text.trim();
		// if (text.startsWith("#ENTRY_POINT"))
		// {
		// return text.substring(text.indexOf('=') + 1).trim();
		// }
		// }
		// } finally
		// {
		// reader.close();
		// }
		// }
		// return null;
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
