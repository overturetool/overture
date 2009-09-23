package org.overturetool.proofsupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import junit.framework.TestCase;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.external_tools.pog.PogGeneratorException;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsPogProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;
import org.overturetool.proofsupport.test.TestSettings;

public class TranslationPreProcessorTest extends TestCase {

	protected static final String VPPDE_BIN = TestSettings.getVppdeBinary();
	private static TestSettings settings = null;
	private static String testModel1 = null;
	private static String testModel2 = null;
	private static String stackModel = null;
	private static String emptyModel = null;
	private static String parseError = null;
	private static String testPogFileNoNewLine = null;
	private static String stackModelPogFile = null;

	protected void setUp() throws Exception {
		super.setUp();
		setUpTestValues();
	}

	private void setUpTestValues() throws Exception {
		settings = new TestSettings();
		testModel1 = settings.get(TestSettings.TEST_MODEL_1);
		testModel2 = settings.get(TestSettings.TEST_MODEL_2);
		stackModel = settings.get(TestSettings.STACK_MODEL);
		emptyModel = settings.get(TestSettings.EMPTY_MODEL);
		parseError = settings.get(TestSettings.PARSE_ERROR);
		testPogFileNoNewLine = settings.get(TestSettings.TEST_POG_FILE_NO_NEW_LINE);
		stackModelPogFile = settings.get(TestSettings.STACK_MODEL_POG_FILE);
	}

	public void testPrepareVdmFiles() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = testModel2;
		int expectedPoSize = 5;
		int expectedContextSize = 1;
		List<String> contextFiles = new ArrayList<String>(expectedContextSize);
		contextFiles.add(testModel1);

		PreparationData actual = prep.prepareVdmFiles(modelFile, contextFiles);
		
		assertPreparationDataIsGood(modelFile, expectedContextSize,
				expectedPoSize, actual);
	}

	public void testPrepareVdmFilesNoContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = stackModel;
		int expectedPoSize = 2;
		int expectedContextSize = 0;
		List<String> contextFiles = new ArrayList<String>(expectedContextSize);

		PreparationData actual = prep.prepareVdmFiles(modelFile, contextFiles);

		assertPreparationDataIsGood(modelFile, expectedContextSize,
				expectedPoSize, actual);
	}
	
	public void testPrepareVdmFilesEmptyModel() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = emptyModel;
		List<String> contextFiles = new ArrayList<String>(0);

		try {
			prep.prepareVdmFiles(modelFile, contextFiles);
			fail("The model was empty and method should have thrown an exception.");
		} catch(PogGeneratorException e) {
			
		}
	}
	
	public void testPrepareVdmFilesCantParse() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = parseError;
		List<String> contextFiles = new ArrayList<String>(0);

		try {
			prep.prepareVdmFiles(modelFile, contextFiles);
			fail("The model doesn't parse and method should have thrown an exception.");
		} catch(PogGeneratorException e) {
			
		}
	}
	
	public void testPrepareVdmFilesNullFile() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = null;
		List<String> contextFiles = new ArrayList<String>(0);

		try {
			prep.prepareVdmFiles(modelFile, contextFiles);
			fail("The model file name was null and method should have thrown an exception.");
		} catch(NullPointerException e) {
			
		}
	}

	public void testGenerateOmlAst() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = testModel2;
		int expectedPoSize = 5;
		int expectedContextSize = 1;
		List<String> contextFiles = new ArrayList<String>(expectedContextSize);
		contextFiles.add(testModel1);
		List<String> poExpressions = prep.processPogFile(testPogFileNoNewLine);

		PreparationData actual = prep.generateOmlAst(modelFile, contextFiles, poExpressions);
		
		assertPreparationDataIsGood(modelFile, expectedContextSize,
				expectedPoSize, actual);
	}

	public void testGenerateOmlAstNoContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = stackModel;
		int expectedPoSize = 2;
		int expectedContextSize = 0;
		List<String> contextFiles = new ArrayList<String>(expectedContextSize);
		List<String> poExpressions = prep.processPogFile(stackModelPogFile);

		PreparationData actual = prep.generateOmlAst(modelFile, contextFiles, poExpressions);

		assertPreparationDataIsGood(modelFile, expectedContextSize,
				expectedPoSize, actual);
	}

	public void testParseContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		List<String> contextFiles = new ArrayList<String>(1);
		contextFiles.add(testModel1);
		contextFiles.add(testModel2);
		List<IOmlDocument> omlDocs = prep.parseContext(contextFiles);

		assertEquals(2, omlDocs.size());
		for (IOmlDocument doc : omlDocs) {
			assertTrue(doc != null);
			assertTrue(doc.toVdmPpValue() != null);
			assertTrue(doc.toVdmPpValue().length() > 0);
		}
	}

	public void testProcessPogFile() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());

		List<String> actual = prep.processPogFile(testPogFileNoNewLine);

		assertEquals(5, actual.size());
		for (String s : actual) {
			assertTrue(s != null);
			assertTrue(s.length() > 0);
		}
	}

	public void testGeneratePogFile() throws Exception {
		String modelFile = testModel2;
		List<String> contextFiles = new ArrayList<String>(1);
		contextFiles.add(testModel1);
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());

		String actual = prep.generatePogFile(modelFile, contextFiles);
		String expected = testModel2 + ".pog";
		File pogFile = new File(expected);

		assertTrue(pogFile.exists());
		assertEquals(expected, actual.trim());
	}

	public void testGeneratePogFileNoContext() throws Exception {
		String modelFile = stackModel;
		List<String> contextFiles = new ArrayList<String>(0);
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());

		String actual = prep.generatePogFile(modelFile, contextFiles);
		String expected = modelFile + ".pog";
		File pogFile = new File(expected);

		assertTrue(pogFile.exists());
		assertEquals(expected, actual.trim());
	}
	
	private void assertPreparationDataIsGood(String modelFile,
			int expectedContextSize, int expectedPoSize, PreparationData actual)
			throws CGException {
		IOmlDocument omlModel = actual.getOmlModel();
		List<IOmlDocument> omlContextDocuments = actual.getOmlContextDocuments();
		List<IOmlExpression> omlPos = actual.getOmlPos();
		
		assertTrue(omlModel != null);
		assertEquals(modelFile, omlModel.getFilename());
		assertTrue(omlModel.toVdmPpValue() != null);
		assertTrue(actual.omlModel.toVdmPpValue().length() > expectedContextSize);

		assertEquals(expectedContextSize, omlContextDocuments.size());
		if(expectedContextSize > 0)
			for (IOmlDocument doc : omlContextDocuments) {
				assertTrue(doc != null);
				assertTrue(doc.toVdmPpValue() != null);
				assertTrue(doc.toVdmPpValue().length() > 0);
			}
		
		assertEquals(expectedPoSize, omlPos.size());
		if(expectedPoSize > 0)
			for (IOmlExpression s : omlPos)
				assertTrue(s != null);
	}
}
