package org.overture.refactor.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.refactor.tests.base.ResultObject;
import org.overture.refactor.tests.base.TestUtils;
import org.overture.refactoring.GeneratedData;
import org.overture.refactoring.RefactoringMain;
import org.overture.rename.Renaming;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Parameterized.class)
public class VarRenamingTest extends GlobalFileTester {
	ObjectMapper mapper = new ObjectMapper();
	private File inputFile;

	public static final String ROOT_INPUT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar + "renamingTestInputs";
	public static final String ROOT_RESULT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar + "renamingTestResults"
			+ File.separatorChar;
				
	public VarRenamingTest(File inputFile){
		super();
		this.inputFile = inputFile;
	}
	
	@Before
	public void init() throws Exception
	{
		Settings.dialect = Dialect.VDM_SL;
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
