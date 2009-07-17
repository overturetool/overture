package org.overturetool.proofsupport.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import org.overturetool.proofsupport.AbstractSettings;
import org.overturetool.proofsupport.maven.Profile;
import org.overturetool.proofsupport.maven.MavenSettings;
import org.xml.sax.SAXException;

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
			if (VPPDE_BIN == null || VPPDE_BIN.length() == 0)
				VPPDE_BIN = s.getDefaultProfile().getProperty(
						Profile.USER_VDMTOOLS_CMD_PATH);
			if (MOSML_DIR == null || MOSML_DIR.length() == 0)
				MOSML_DIR = s.getDefaultProfile().getProperty(
						Profile.USER_MOS_ML_DIR);
			if (HOL_DIR == null || HOL_DIR.length() == 0)
				HOL_DIR = s.getDefaultProfile().getProperty(
						Profile.USER_HOL_DIR);
		} catch (Exception e)
		{
			throw new IllegalArgumentException(
					"In order to execute the unit tests you need to supply the following VM arguments:\n"
							+ "-Dproofsupport.vppde.bin=\"<value>\"\n"
							+ "-Dproofsupport.mosml.dir=\"<value>\"\n"
							+ "-Dproofsupport.hol.dir=\"<value>\"\n", e);
		}
	}

	protected static final String SETTINGS_FILE = "src/test/java/org/overturetool/proofsupport/test/Settings.xml";
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
