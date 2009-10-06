package org.overturetool.proofsupport;

import junit.framework.TestCase;

import org.overturetool.proofsupport.test.TestSettings;

public class ApsMainTest extends TestCase {

	public void testMain() throws Exception {
		TestSettings settings = new TestSettings();
		String[] args = new String[] { TestSettings.getMosmlDir(), "-vppde",
				TestSettings.getVppdeBinary(), settings.get(TestSettings.SET_MODEL) };
		ApsMain.main(args);
	}
	
	public void testMainProof() throws Exception {
		TestSettings settings = new TestSettings();
		String[] args = new String[] { "-p", "-hol", TestSettings.getHolDir(),
				"-mosml", TestSettings.getMosmlDir(), "-vppde",
				TestSettings.getVppdeBinary(), settings.get(TestSettings.SET_MODEL) };
		ApsMain.main(args);
	}
	
	public void testMainTranslate() throws Exception {
		TestSettings settings = new TestSettings();
		String[] args = new String[] { "-t", "-vppde",
				TestSettings.getVppdeBinary(), settings.get(TestSettings.SET_MODEL) };
		ApsMain.main(args);
	}

}
