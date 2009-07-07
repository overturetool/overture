package org.overturetool.proofsupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.test.TestSettings;
import org.overturetool.proofsupport.PreparationData;
import org.overturetool.proofsupport.TranslationPreProcessor;
import org.overturetool.proofsupport.external_tools.VdmToolsPogProcessor;
import org.overturetool.proofsupport.external_tools.VdmToolsWrapper;

public class TranslationPreProcessorTest extends TestCase {

	protected static final String VPPDE_BIN = TestSettings.getVppdeBinary();
	private static TestSettings settings = null;
	private static String testModel1 = null;
	private static String testModel2 = null;
	private static String stackModel = null;
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
		testPogFileNoNewLine = settings.get(TestSettings.TEST_POG_FILE_NO_NEW_LINE);
		stackModelPogFile = settings.get(TestSettings.STACK_MODEL_POG_FILE);
	}

	// TODO refactor this test method so that it doesn't violate PreparationData
	// encapsulation!
	public void testPrepareVdmFiles() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = testModel2;
		List<String> contextFiles = new ArrayList<String>(1);
		contextFiles.add(testModel1);

		PreparationData actual = prep.prepareVdmFiles(modelFile, contextFiles);

		assertTrue(actual.omlModel != null);
		assertEquals(modelFile, actual.omlModel.getFilename());
		assertTrue(actual.omlModel.toVdmPpValue() != null);
		assertTrue(actual.omlModel.toVdmPpValue().length() > 0);

		assertEquals(1, actual.omlContextDocuments.size());
		for (IOmlDocument doc : actual.omlContextDocuments) {
			assertTrue(doc != null);
			assertTrue(doc.toVdmPpValue() != null);
			assertTrue(doc.toVdmPpValue().length() > 0);
		}

		assertEquals(5, actual.omlPos.size());
		for (IOmlExpression s : actual.omlPos)
			assertTrue(s != null);
	}

	// TODO refactor this test method so that it doesn't violate PreparationData
	// encapsulation!
	public void testPrepareVdmFilesNoContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = stackModel;
		List<String> contextFiles = new ArrayList<String>(0);

		PreparationData actual = prep.prepareVdmFiles(modelFile, contextFiles);

		assertTrue(actual.omlModel != null);
		assertEquals(modelFile, actual.omlModel.getFilename());
		assertTrue(actual.omlModel.toVdmPpValue() != null);
		assertTrue(actual.omlModel.toVdmPpValue().length() > 0);

		assertEquals(0, actual.omlContextDocuments.size());

		assertEquals(2, actual.omlPos.size());
		for (IOmlExpression s : actual.omlPos)
			assertTrue(s != null);
	}

	// TODO refactor this test method so that it doesn't violate PreparationData
	// encapsulation!
	public void testGenerateOmlAst() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = testModel2;
		List<String> contextFiles = new ArrayList<String>(1);
		contextFiles.add(testModel1);
		List<String> poExpressions = prep.processPogFile(testPogFileNoNewLine);

		PreparationData actual = prep.generateOmlAst(modelFile, contextFiles, poExpressions);

		assertTrue(actual.omlModel != null);
		assertEquals(modelFile, actual.omlModel.getFilename());
		assertTrue(actual.omlModel.toVdmPpValue() != null);
		assertTrue(actual.omlModel.toVdmPpValue().length() > 0);

		assertEquals(1, actual.omlContextDocuments.size());
		for (IOmlDocument doc : actual.omlContextDocuments) {
			assertTrue(doc != null);
			assertTrue(doc.toVdmPpValue() != null);
			assertTrue(doc.toVdmPpValue().length() > 0);
		}

		assertEquals(5, actual.omlPos.size());
		for (IOmlExpression s : actual.omlPos)
			assertTrue(s != null);
	}

	// TODO refactor this test method so that it doesn't violate PreparationData
	// encapsulation!
	public void testGenerateOmlAstNoContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				VPPDE_BIN), new VdmToolsPogProcessor());
		String modelFile = stackModel;
		List<String> contextFiles = new ArrayList<String>(0);
		List<String> poExpressions = prep.processPogFile(stackModelPogFile);

		PreparationData actual = prep.generateOmlAst(modelFile, contextFiles, poExpressions);

		assertTrue(actual.omlModel != null);
		assertEquals(modelFile, actual.omlModel.getFilename());
		assertTrue(actual.omlModel.toVdmPpValue() != null);
		assertTrue(actual.omlModel.toVdmPpValue().length() > 0);

		assertEquals(0, actual.omlContextDocuments.size());

		assertEquals(2, actual.omlPos.size());
		for (IOmlExpression s : actual.omlPos)
			assertTrue(s != null);
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
}
