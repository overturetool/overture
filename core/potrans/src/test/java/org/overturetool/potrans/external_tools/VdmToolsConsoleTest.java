package org.overturetool.potrans.external_tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

public class VdmToolsConsoleTest extends TestCase {

private final static String VPPDE_JVM_PROERTY = "-DvppdeExecutable=<path to executable>";
	
	private final static String VPPDE_PROPERTY_ERROR_MESSAGE = 
		"You have to set the flag " + VPPDE_JVM_PROERTY + " for the JVM in"
			+ " the JUnit launch configuration.";

	private final static String settingsWarning = 
		"If this test fails check that you have the correct vaules set in Settings.xml, " +
		"namelly for the test VPP models. Also check that you set the property " +
		"\"" + VPPDE_JVM_PROERTY + "\" in the JVM arguments.";
	
	private final static String pogExtension = ".pog";
	
	
	private final static String vppdeExecutable = System.getProperty("vppdeExecutable");
	
	private static String testModel1 = null;
	private static String testModel2 = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		setUpPreferences();
		InputValidator.validateStringNotEmptyNorNull(vppdeExecutable,
				VPPDE_PROPERTY_ERROR_MESSAGE);
		
		// remove previously generated files
		removePreviousTestsData();
	}
	
	/**
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 * @throws FileNotFoundException
	 */
	private void setUpPreferences() throws IOException,
			InvalidPreferencesFormatException, FileNotFoundException {
		Preferences.importPreferences(new BufferedInputStream(new FileInputStream("Settings.xml")));
		Preferences preferences = Preferences.userNodeForPackage(this.getClass());
		testModel1 = preferences.get("testModel1", null);
		testModel2 = preferences.get("testModel2", null);
	}
	
	/**
	 * 
	 */
	private void removePreviousTestsData() throws Exception {
		File testModel1Pog = new File(testModel1 + pogExtension);
		File testModel2Pog = new File(testModel2 + pogExtension);
		
		if(testModel1Pog.exists()) {
			testModel1Pog.delete();
		}
		
		if(testModel2Pog.exists()) {
			testModel2Pog.delete();
		}
	}

	public void testGeneratePogFile() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		String expected = 
			  "Parsing \"testinput/dosort.vpp\" (Latex) ... done"
			+ SystemProperties.LINE_SEPARATOR
			+ "Parsing \"testinput/sorter.vpp\" (Plain Text) ... done"
			+ SystemProperties.LINE_SEPARATOR
			+ "Type checking Sorter ... done"
			+ SystemProperties.LINE_SEPARATOR
			+ "Type checking DoSort ... done"
			+ SystemProperties.LINE_SEPARATOR
			+ "Class Sorter with super classes are POS type correct"
			+ SystemProperties.LINE_SEPARATOR
			+ "Generating proof obligations for DoSort...done"
			+ SystemProperties.LINE_SEPARATOR
			+ "Generating proof obligations for Sorter...done";

		String actual = VdmToolsConsole.generatePogFile(vppdeExecutable, vdmFiles);
		String message = "The method invocation should have resulted in a new  "
				+ testModel2 + pogExtension + " file." + SystemProperties.LINE_SEPARATOR
				+ settingsWarning;
		File pogFile = new File(testModel2 + pogExtension);
		
		assertTrue(message, pogFile.exists());
		assertEquals(settingsWarning, expected, actual.trim());
	}

}
