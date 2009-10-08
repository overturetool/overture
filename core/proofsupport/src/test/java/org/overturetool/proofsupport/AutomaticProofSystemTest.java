package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.overturetool.proofsupport.external_tools.pog.VdmToolsPoProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;
import org.overturetool.proofsupport.test.TestSettings;

public class AutomaticProofSystemTest extends TestCase {

	private static TestSettings testSettings = null;
	private static String setModel = null;
	private static String stackModel = null;
	private static String vppdeExecutable = TestSettings.getVppdeBinary();
	private static String mosmlDir = TestSettings.getMosmlDir();
	private static String holDir = TestSettings.getHolDir();

	private static String testPogFileNoNewLine = null;
	protected static final String VPPDE_BIN = TestSettings.getVppdeBinary();

	protected void setUp() throws Exception {
		super.setUp();
		setUpTestValues();
	}

	private void setUpTestValues() throws Exception {
		testSettings = new TestSettings();
		setModel = testSettings.get(TestSettings.SET_MODEL);
		stackModel = testSettings.get(TestSettings.STACK_MODEL);
		testPogFileNoNewLine = testSettings
				.get(TestSettings.TEST_POG_FILE_NO_NEW_LINE);
	}

	public void testDischargeAllPosSet() throws Exception {
		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPoProcessor());
		List<String> contextFiles = new ArrayList<String>(0);

		String[] expected = new String[] { "> val it = 1 : int",
				"> val it = 0 : int" };
		String[] actual = aps.dischargeAllPos(setModel, contextFiles);

		assertEquals(23, actual.length);
		assertEquals(expected[0], actual[actual.length - 2]);
		assertEquals(expected[1], actual[actual.length - 1]);

	}

	public void testDischargeAllPosStack() throws Exception {
		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPoProcessor());
		List<String> contextFiles = new ArrayList<String>(0);
		String[] expected = new String[] { "> val it = 2 : int",
				"> val it = 1 : int" };

		String[] actual = aps.dischargeAllPos(stackModel, contextFiles);

		assertEquals(20, actual.length);
		assertEquals(expected[0], actual[actual.length - 2]);
		assertEquals(expected[1], actual[actual.length - 1]);
	}

	public void testDoModelTranslation() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(VPPDE_BIN), new VdmToolsPoProcessor());
		String modelFile = setModel;
		List<String> contextFiles = new ArrayList<String>(0);
		PreparationData prepData = prep.prepareVdmFiles(modelFile, contextFiles);
		
		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPoProcessor());
		String holCode = aps.doModelTranslation(prepData);

		assertNotNull(holCode);
		assertEquals(1210, holCode.length());
	}
}
