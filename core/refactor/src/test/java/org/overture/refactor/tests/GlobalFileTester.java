package org.overture.refactor.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.definitions.SClassDefinition;
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

	protected void globalTest(File inputFile, String ROOT_RESULT, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException{
		TypeCheckResult<List<SClassDefinition>> originalSpecTcResult = TypeCheckerUtil.typeCheckPp(inputFile);
		
		Assert.assertTrue(inputFile.getName() + " has type errors", originalSpecTcResult.errors.isEmpty());
		
		String resultFilePath = ROOT_RESULT + inputFile.getName() + ".json";
		
		//JSON from file to Object
		List<ResultObject> objs = mapper.readValue(new File(resultFilePath), new TypeReference<List<ResultObject>>(){});
		
		for(Iterator<ResultObject> iter = objs.iterator(); iter.hasNext();){
			ResultObject resObj = iter.next();
			String languageStr = resObj.getLanguage();
			String configStr = resObj.getConfig();
			
			String[] strArr = {TEST_ARG, languageStr,configStr,inputFile.getAbsolutePath()};
			RefactoringMain.main(strArr);

			GeneratedData genData = RefactoringMain.getGeneratedData();
			if(genData == null){
				System.out.println("There was not generated any data!");
				Assert.assertTrue(genData == null);
			}
			
			//RENAME CHECK
			List<BasicRefactoringType> renamings =(List<BasicRefactoringType>)(List<?>) genData.getAllRenamings();	
			List<String> renamingStrings = removeFilePathFromText(renamings);			
			checkAssertions(resObj.getRenamings(), renamingStrings);
			
			//EXTRACT CHECK
			List<BasicRefactoringType> extractions =(List<BasicRefactoringType>)(List<?>) genData.getAllExtractions();
			List<String> extractionStrings = removeFilePathFromText(extractions);
			checkAssertions(resObj.getExtractions(), extractionStrings);
			
			//SIGNATURE CHANGE CHECK
			List<BasicRefactoringType> signatureChanges =(List<BasicRefactoringType>)(List<?>) genData.getAllSignatureChanges();
			List<String> signatureChangeStrings = removeFilePathFromText(signatureChanges);
			checkAssertions(resObj.getSignatureChanges(), signatureChangeStrings);
			
			//UNREACHABLE CODE REMOVE CHECKER
			List<BasicRefactoringType> removedStm =(List<BasicRefactoringType>)(List<?>) genData.getAllRemovals();
			List<String> removedStmStrings = removeFilePathFromText(removedStm);
			checkAssertions(resObj.getUnreachableStmRemoved(), removedStmStrings);
			
			//CONVERT FUNCTION TO OPERATION CHECKER
			List<BasicRefactoringType> conversionFromFuncToOp =(List<BasicRefactoringType>)(List<?>) genData.getAllConversionFromFuncToOp();
			List<String> conversionFromFuncToOpStrings = removeFilePathFromText(conversionFromFuncToOp);
			checkAssertions(resObj.getConvertedFunctionToOperation(), conversionFromFuncToOpStrings);
		}
	}

	private void checkAssertions(List<String> resObj, List<String> conversionFromFuncToOpStrings) {
		Assert.assertTrue((resObj == null && (conversionFromFuncToOpStrings == null || conversionFromFuncToOpStrings.isEmpty())) || 
				resObj.size() == conversionFromFuncToOpStrings.size());
		for(int i = 0; i < conversionFromFuncToOpStrings.size();i++ ) {
			String item = conversionFromFuncToOpStrings.get(i);
			Assert.assertTrue(resObj.contains(item));
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
}

