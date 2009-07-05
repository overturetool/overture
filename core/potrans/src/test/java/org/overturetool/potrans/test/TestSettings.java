package org.overturetool.potrans.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import org.overturetool.potrans.proof_system.AbstractSettings;

public class TestSettings extends AbstractSettings {

	protected static final String SETTINGS_FILE = "src/test/java/org/overturetool/potrans/test/Settings.xml";
	protected static final String VPPDE_BIN = System.getProperty("potrans.vppde.bin");
	protected static final String MOSML_DIR = System.getProperty("potrans.mosml.dir");
	protected static final String HOL_DIR = System.getProperty("potrans.hol.dir");
	protected static final String OS_NAME = System.getProperty("os.name");

	protected static final String CAT_PROGRAM;
	static {
		if(OS_NAME.startsWith("Windows"))
			CAT_PROGRAM = "more.com";
		else
			CAT_PROGRAM = "cat";
	}
	
	protected static final String HOL_BIN;
	static {
		if(OS_NAME.startsWith("Windows"))
			HOL_BIN = "hol.bat";
		else
			HOL_BIN = "hol";
	}


	public static final String SET_MODEL = "setModel";
	public static final String STACK_MODEL = "stackModel";
	public static final String TEST_MODEL_1 = "testModel1";
	public static final String TEST_MODEL_2 = "testModel2";
	public static final String TEST_MODEL_3 = "testModel3";
	public static final String TEST_THEORY = "testTheory1";
	public static final String TEST_POG_FILE_NO_NEW_LINE = "testPogFileNoNewLine";
	public static final String TEST_POG_FILE_WITH_NEW_LINE = "testPogFileWithNewLine";
	public static final String STACK_MODEL_POG_FILE = "stackModelPogFile";
	public static final String VDM_HOL_TACTICS = "vdmHolTactics";
	public static final String TEST_DATA_DIR = "testDataDir";
	
	public TestSettings() throws FileNotFoundException, IOException, InvalidPreferencesFormatException,
			BackingStoreException {
		super(SETTINGS_FILE);
	}
	
	public static String getVppdeBinary() {
		return VPPDE_BIN;
	}

	public static String getMosmlDir() {
		return MOSML_DIR;
	}

	public static String getHolDir() {
		return HOL_DIR;
	}
	
	public static String getOsName() {
		return OS_NAME;
	}

	public static String getCatProgram() {
		return CAT_PROGRAM;
	}
	
	public static String getHolBin() {
		return HOL_BIN;
	}

	public static ArrayList<String> getNopProgramCommand() {
		ArrayList<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-version");
		return command;
	}
}
