package org.overturetool.proofsupport.test;

import org.overturetool.proofsupport.external_tools.hol.HolParameters;

import junit.framework.TestCase;

public abstract class AutomaticProofSystemTestCase extends TestCase {

	protected static TestSettings testSettings = null;
	
	protected static final String TEST_MESSAGE = "This is a test!";
	protected static final String INVALID_FILE_NAME = "invalid_file_name";
	protected static final String POG_FILE_EXTENSION = ".pog";
	
	protected static String doSortModel = null;
	protected static String sorterModel = null;
	protected static String setModel = null;
	protected static String stackModel = null;
	protected static String emptyModel = null;
	
	protected static String parseErrorModel = null;
	protected static String testPogFileNoNewLine = null;
	protected static String testPogFileWithNewLine = null;
	protected static String stackModelPogFile = null;
	
	protected static String vppdeExecutable = TestSettings.getVppdeBinary();
	protected static String mosmlDir = TestSettings.getMosmlDir();
	protected static String holDir = TestSettings.getHolDir();
	
	protected final static HolParameters holParam = new HolParameters(
			mosmlDir, holDir);

	protected static final String VPPDE_BIN = TestSettings.getVppdeBinary();

	protected void setUp() throws Exception {
		super.setUp();
		setUpTestValues();
	}

	private void setUpTestValues() throws Exception {
		testSettings = new TestSettings();
		
		doSortModel = testSettings.get(TestSettings.DO_SORT_MODEL);
		sorterModel = testSettings.get(TestSettings.SORTER_MODEL);
		setModel = testSettings.get(TestSettings.SET_MODEL);
		stackModel = testSettings.get(TestSettings.STACK_MODEL);
		emptyModel = testSettings.get(TestSettings.EMPTY_MODEL);
		parseErrorModel = testSettings.get(TestSettings.PARSE_ERROR_MODEL);
		
		testPogFileNoNewLine = testSettings.get(TestSettings.TEST_POG_FILE_NO_NEW_LINE);
		testPogFileWithNewLine = testSettings.get(TestSettings.TEST_POG_FILE_WITH_NEW_LINE);
		stackModelPogFile = testSettings.get(TestSettings.STACK_MODEL_POG_FILE);
	}
	
}

