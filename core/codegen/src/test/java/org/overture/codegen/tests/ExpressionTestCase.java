package org.overture.codegen.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.Assert;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.constants.IText;
import org.overture.codegen.vdmcodegen.CodeGenUtil;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.test.framework.BaseTestCase;
import org.overture.test.framework.Properties;

public class ExpressionTestCase extends BaseTestCase
{
	private File file;
	private String content;

	public ExpressionTestCase()
	{
		super();
	}

	public ExpressionTestCase(File file)
	{
		super(file);
		this.file = file;
		this.content = file.getName();

	}

	@Override
	public String getName()
	{
		return this.content;
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	public void test() throws ParserException, LexException, IOException
	{
		if (content != null)
		{
			String fileContent = CodeGenTestUtil.getFileContent(file);
	
			try
			{
				String result = CodeGenUtil.generateFromExp(fileContent);
				compareResults(result, file.getAbsolutePath());
			} catch (AnalysisException e)
			{
				Assert.fail("Code could not be generated from the file: " + e.getMessage());
			}
		}
	}

	private void compareResults(String result, String filename)
	{
		if (Properties.recordTestResults)
		{
			File resultFile = createResultFile(filename);
			resultFile.getParentFile().mkdirs();
			
			try
			{
				String input = CodeGenTestUtil.getFileContent(file);
				String newResult = CodeGenUtil.generateFromExp(input);
				storeResult(resultFile, newResult);
				
			} catch (FileNotFoundException |  AnalysisException e)
			{
				Assert.fail("The produced results could not be stored: " + e.getMessage());
			}
			
			return;
		}

		File file = getResultFile(filename);

		assertNotNull("Result file " + file.getName() + " was not found", file);
		assertTrue("Result file " + file.getAbsolutePath() + " does not exist", file.exists());

		String parsedResult = CodeGenTestUtil.getFileContent(file);
		boolean parsed = parsedResult != null;

		Assert.assertTrue("Could not read result file: " + file.getName(), parsed);

		boolean resultOk = result.equals(parsedResult);
		Assert.assertTrue("The code generator did not produce the expected output. "
				+ IText.NEW_LINE
				+ "Expected: "
				+ parsedResult
				+ IText.NEW_LINE
				+ "Actual: " + result, resultOk);
	}

	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}
	
	protected void storeResult(File file, String result)
			throws FileNotFoundException
	{
		PrintStream out = new PrintStream(new FileOutputStream(file));
		out.print(result);
		out.close();
	}
	
}
