package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.overturetool.proofsupport.external_tools.pog.VdmToolsPogProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;
import org.overturetool.proofsupport.test.TestSettings;

public class AutomaticProofSystemTest extends TestCase {

	private static TestSettings testSettings = null;
	private static String setModel = null;
	private static String stackModel = null;
	private static String vppdeExecutable = TestSettings.getVppdeBinary();
	private static String mosmlDir = TestSettings.getMosmlDir();
	private static String holDir = TestSettings.getHolDir();

	protected void setUp() throws Exception {
		super.setUp();
		
		setUpTestValues();
	}

	private void setUpTestValues() throws Exception {
		testSettings = new TestSettings();
		setModel = testSettings.get(TestSettings.SET_MODEL);
		stackModel = testSettings.get(TestSettings.STACK_MODEL);
	}

	// TODO add asserts
	public void testDischargeAllPosSet() throws Exception {
		AutomaticProofSystem aps = new AutomaticProofSystem(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPogProcessor());
		List<String> contextFiles = new ArrayList<String>(0);
		String[] actual = aps.dischargeAllPos(setModel, contextFiles);
		for (String s : actual)
			System.err.println(s);
	}

	public void testDischargeAllPosStack() throws Exception {
		AutomaticProofSystem aps = new AutomaticProofSystem(mosmlDir,
				holDir, new VdmToolsWrapper(vppdeExecutable),
				new VdmToolsPogProcessor());
		List<String> contextFiles = new ArrayList<String>(0);
		String[] expected = new String[] { "> val it = 2 : int", "> val it = 1 : int" };

		String[] actual = aps.dischargeAllPos(stackModel, contextFiles);

		assertEquals(20, actual.length);
		assertEquals(expected[0], actual[actual.length - 2]);
		assertEquals(expected[1], actual[actual.length - 1]);
	}

}
