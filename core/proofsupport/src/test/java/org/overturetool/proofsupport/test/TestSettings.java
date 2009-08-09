package org.overturetool.proofsupport.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import org.overturetool.proofsupport.AbstractSettings;
import org.overturetool.proofsupport.test.maven.MavenSettings;
import org.overturetool.proofsupport.test.maven.Profile;

public class TestSettings extends AbstractSettings
{

	protected static String VPPDE_BIN = System.getProperty("proofsupport.vppde.bin");
	protected static String MOSML_DIR = System.getProperty("proofsupport.mosml.dir");
	protected static String HOL_DIR = System.getProperty("proofsupport.hol.dir");
	static
	{
		try
		{
			MavenSettings s = new MavenSettings();
			VPPDE_BIN = checkAndSetValue(s, VPPDE_BIN, Profile.USER_VPPDE_BIN);
			MOSML_DIR = checkAndSetValue(s, MOSML_DIR, Profile.USER_MOSML_DIR);
			HOL_DIR = checkAndSetValue(s, HOL_DIR, Profile.USER_HOL_DIR);
		} catch (Exception e)
		{
			throw new IllegalArgumentException(
					"In order to execute the unit tests you need to supply the following VM arguments:\n"
							+ "-Dproofsupport.vppde.bin=\"<value>\"\n"
							+ "-Dproofsupport.mosml.dir=\"<value>\"\n"
							+ "-Dproofsupport.hol.dir=\"<value>\"\n", e);
		}
	}

	private static String checkAndSetValue(MavenSettings s, String value, String mvnParameter) {
		if (!isString(value))
			return setValueFromMavenSettings(s, mvnParameter);
		else
			return value;
	}

	private static String setValueFromMavenSettings(MavenSettings s, String mvnParameter) {
		return s.getDefaultProfile().getProperty(mvnParameter);
	}

	protected static boolean isString(String s) {
		return s != null && s.length() != 0;
	}

	protected static final String SETTINGS_FILE = "Settings.xml";
	protected static final String OS_NAME = System.getProperty("os.name");

	protected static final String CAT_PROGRAM;
	static
	{
		if (OS_NAME.startsWith("Windows"))
			CAT_PROGRAM = "more.com";
		else
			CAT_PROGRAM = "cat";
	}

	protected static final String HOL_BIN;
	static
	{
		if (OS_NAME.startsWith("Windows"))
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

	public TestSettings() throws FileNotFoundException, IOException,
			InvalidPreferencesFormatException, BackingStoreException
	{
		super(SETTINGS_FILE);

	}

	public static String getVppdeBinary()
	{
		return VPPDE_BIN;
	}

	public static String getMosmlDir()
	{
		return MOSML_DIR;
	}

	public static String getHolDir()
	{
		return HOL_DIR;
	}

	public static String getOsName()
	{
		return OS_NAME;
	}

	public static String getCatProgram()
	{
		return CAT_PROGRAM;
	}

	public static String getHolBin()
	{
		return HOL_BIN;
	}

	public static ArrayList<String> getNopProgramCommand()
	{
		ArrayList<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-version");
		return command;
	}
}
