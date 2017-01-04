package org.overture.refactor.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.refactor.tests.base.ResultObject;
import org.overture.refactoring.BasicRefactoringType;
import org.overture.refactoring.GeneratedData;
import org.overture.refactoring.RefactoringMain;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GlobalFileTester {

	protected static final String TEST_ARG = "-test";

	@SuppressWarnings("unchecked")
	protected void globalTest(File inputFile, String ROOT_RESULT, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException{
		TypeCheckResult<List<SClassDefinition>> originalSpecTcResult = TypeCheckerUtil.typeCheckPp(inputFile);
		
		Assert.assertTrue(inputFile.getName() + " has type errors", originalSpecTcResult.errors.isEmpty());
		
		String resultFilePath = ROOT_RESULT + inputFile.getName() + ".json";
		
		Settings.dialect = Dialect.VDM_SL;
        Settings.release = Release.VDM_10;

        Value beforeValue = null;
        Value afterValue = null;
        
		try {
			beforeValue = InterpreterUtil.interpret(Settings.dialect, "DEFAULT`run()", inputFile);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		//JSON from file to Object
		List<ResultObject> objs = mapper.readValue(new File(resultFilePath), new TypeReference<List<ResultObject>>(){});
		
		for(Iterator<ResultObject> iter = objs.iterator(); iter.hasNext();){
			ResultObject resObj = iter.next();
			
			String[] strArr = {TEST_ARG,"-print", resObj.getLanguage(), resObj.getConfig(), inputFile.getAbsolutePath()};
			RefactoringMain.main(strArr);

			GeneratedData genData = RefactoringMain.getGeneratedData();
			if(genData == null){
				System.out.println("There was not generated any data!");
				Assert.assertTrue(genData == null);
			}
			
			//RENAME CHECK
			List<BasicRefactoringType> renamings = (List<BasicRefactoringType>)(List<?>) genData.getAllRenamings();	
			List<String> renamingStrings = removeFilePathFromText(renamings);			
			checkAssertions(resObj.getRenamings(), renamingStrings);
			
			//EXTRACT CHECK
			List<BasicRefactoringType> extractions = (List<BasicRefactoringType>)(List<?>) genData.getAllExtractions();
			List<String> extractionStrings = removeFilePathFromText(extractions);
			checkAssertions(resObj.getExtractions(), extractionStrings);
			
			//SIGNATURE CHANGE CHECK
			List<BasicRefactoringType> signatureChanges = (List<BasicRefactoringType>)(List<?>) genData.getAllSignatureChanges();
			List<String> signatureChangeStrings = removeFilePathFromText(signatureChanges);
			checkAssertions(resObj.getSignatureChanges(), signatureChangeStrings);
			
			//UNREACHABLE CODE REMOVE CHECKER
			List<BasicRefactoringType> removedStm = (List<BasicRefactoringType>)(List<?>) genData.getAllRemovals();
			List<String> removedStmStrings = removeFilePathFromText(removedStm);
			checkAssertions(resObj.getUnreachableStmRemoved(), removedStmStrings);
			
			//CONVERT FUNCTION TO OPERATION CHECKER
			List<BasicRefactoringType> conversionFromFuncToOp = (List<BasicRefactoringType>)(List<?>) genData.getAllConversionFromFuncToOp();
			List<String> conversionFromFuncToOpStrings = removeFilePathFromText(conversionFromFuncToOp);
			checkAssertions(resObj.getConvertedFunctionToOperation(), conversionFromFuncToOpStrings);
		}
		
		String filePath = inputFile.getPath();
		filePath = filePath.replaceAll(".vdmsl", "Test.vdmsl");
		File afterFile = new File(filePath);
		
		try {
			afterValue = InterpreterUtil.interpret(Settings.dialect, "DEFAULT`run()", afterFile);
			afterFile.delete();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
			afterFile.delete();
		}
		
	    if(beforeValue != null || afterValue != null)
        {
            Assert.assertEquals(beforeValue, afterValue);
        }else{
        	Assert.fail("VDM results are null");
        }
	}

	private void checkAssertions(List<String> resultObj, List<String> stringsGeneratedByRefactoring) {
		Assert.assertTrue((resultObj == null && (stringsGeneratedByRefactoring == null || stringsGeneratedByRefactoring.isEmpty())) || 
				resultObj.size() == stringsGeneratedByRefactoring.size());
		for(int i = 0; i < stringsGeneratedByRefactoring.size(); i++ ) {
			String item = stringsGeneratedByRefactoring.get(i);
			Assert.assertTrue(resultObj.contains(item));
		}
	}
	
	private List<String> removeFilePathFromText(List<BasicRefactoringType> obj){
		
		List<String> finishedStrings = new ArrayList<>();
		if(obj != null){
			for(Iterator<BasicRefactoringType> i = obj.iterator(); i.hasNext(); ) {
				BasicRefactoringType item = i.next();
				finishedStrings.add(item.toString().replaceAll("\\(.*?\\) ?", ""));
			}
		}
		return finishedStrings;	
	}
	
	public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
	    try {
	        return clazz.cast(o);
	    } catch(ClassCastException e) {
	        return null;
	    }
	}
}

