package org.overture.refactor.tests;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.refactor.tests.base.TestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Parameterized.class)
public class ExtractionTest extends GlobalFileTester{
	ObjectMapper mapper = new ObjectMapper();
	private File inputFile;
	public static final String ROOT_INPUT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar + "extractTestInputs";
	public static final String ROOT_RESULT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar + "extractTestResults"
			+ File.separatorChar;
				
	public ExtractionTest(File inputFile){
		super();
		this.inputFile = inputFile;
	}
	
	@Before
	public void init() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}
	
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return TestUtils.collectFiles(ROOT_INPUT);
	}
	
	@Test
	public void test() throws Exception
	{
		globalTest(inputFile, ROOT_RESULT, mapper);
	}
}
