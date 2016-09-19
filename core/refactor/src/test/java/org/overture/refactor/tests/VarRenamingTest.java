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
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.refactor.tests.base.ResultObject;
import org.overture.refactor.tests.base.TestUtils;
import org.overture.refactoring.GeneratedData;
import org.overture.refactoring.RefactoringMain;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Parameterized.class)
public class VarRenamingTest {
	ObjectMapper mapper = new ObjectMapper();
	private File inputFile;
	public static final String ROOT_INPUT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar + "renamingTestInputs";
	public static final String ROOT_RESULT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar + "renamingTestResults"
			+ File.separatorChar;
				
	public VarRenamingTest(File inputFile){
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
		TypeCheckResult<List<SClassDefinition>> originalSpecTcResult = TypeCheckerUtil.typeCheckPp(inputFile);
		
		Assert.assertTrue(inputFile.getName() + " has type errors", originalSpecTcResult.errors.isEmpty());
		
		String resultFilePath = ROOT_RESULT + inputFile.getName() + ".json";
		
		//JSON from file to Object
		List<ResultObject> objs = mapper.readValue(new File(resultFilePath), new TypeReference<List<ResultObject>>(){});
		
		for(Iterator<ResultObject> iter = objs.iterator(); iter.hasNext();){
			ResultObject resObj = iter.next();
			String configStr = resObj.getConfig();
			List<String> resultRenamings = resObj.getRenamings();

			String[] strArr = {"-pp",configStr,inputFile.getAbsolutePath()};
			RefactoringMain.main(strArr);

			GeneratedData genData = RefactoringMain.getGeneratedData();
			List<Renaming> renamings = genData.getAllRenamings();

			List<String> renamingStrings = removeFilePathFromRenaming(renamings);

			Assert.assertTrue((resultRenamings == null && renamings == null) || resultRenamings.size() == renamings.size());

			for(int i = 0; i < renamingStrings.size();i++ ) {
				String item = renamingStrings.get(i);
				System.out.println(item);
				Assert.assertTrue(resultRenamings.contains(item));
			}
		}
	}
	
	private List<String> removeFilePathFromRenaming(List<Renaming> renamings){
		List<String> renamedStrings = new ArrayList<>();
		for(Iterator<Renaming> i = renamings.iterator(); i.hasNext(); ) {
			   Renaming item = i.next();
			   renamedStrings.add(item.toString().replaceAll("\\(.*\\)", ""));
			}
		return renamedStrings;
		
	}
}
