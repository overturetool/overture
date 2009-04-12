/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

/**
 * @author miguel_ferreira
 *
 */
public class VdmToolsWrapperTest extends TestCase {

	private final static String newLine = System.getProperty("line.separator");

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
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		setUpPreferences();
		InputValidator.validateStringNotEmptyNorNull(vppdeExecutable,
				VPPDE_PROPERTY_ERROR_MESSAGE);
		
		// remove previously generated files
		removePreviousTestsData();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
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
			+ newLine
			+ "Parsing \"testinput/sorter.vpp\" (Plain Text) ... done"
			+ newLine
			+ "Type checking Sorter ... done"
			+ newLine
			+ "Type checking DoSort ... testinput/dosort.vpp, l. 23, c. 3:"
			+ newLine
			+ "  Warning[412] : \"DoSorting\" is recursive but does not " +
					"have a measure defined"
			+ newLine
			+ "testinput/dosort.vpp, l. 31, c. 3:"
			+ newLine
			+ "  Warning[412] : \"InsertSorted\" is recursive but does not " +
					"have a measure defined"
			+ newLine
			+ "Warnings: 2"
			+ newLine
			+ "done"
			+ newLine
			+ "Class Sorter with super classes are POS type correct"
			+ newLine
			+ "Generating proof obligations for DoSort...done"
			+ newLine
			+ "Generating proof obligations for Sorter...done";

		String actual = VdmToolsWrapper.generatePogFile(vppdeExecutable, vdmFiles);
		String message = "The method invocation should have resulted in a new  "
				+ testModel2 + pogExtension + " file." + newLine
				+ settingsWarning;
		File pogFile = new File(testModel2 + pogExtension);
		assertTrue(message, pogFile.exists());
		assertEquals(settingsWarning, expected, actual.trim());
	}
	
	public void testGeneratePogFileInvalidVdmFile() throws Exception {
		String vdmFile = "some_invalid_file";
		String[] vdmFiles = new String[]{ vdmFile };
		String expectedStart = "Couldn't open file ";

		String actual = VdmToolsWrapper.generatePogFile(vppdeExecutable, vdmFiles);
		assertTrue(settingsWarning, actual.trim().startsWith(expectedStart));
		assertTrue(settingsWarning, actual.trim().endsWith(vdmFile + "'"));
	}
	
	public void testGeneratePogFileEmptyVdmFiles() throws Exception {
		String[] vdmFiles = new String[]{ };
		
		try {
			VdmToolsWrapper.generatePogFile(vppdeExecutable, vdmFiles);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVdmFiles() throws Exception {
		String[] vdmFiles = null;
		
		try {
			VdmToolsWrapper.generatePogFile(vppdeExecutable, vdmFiles);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVdmFile() throws Exception {
		String vdmFile = null;
		String[] vdmFiles = new String[] { vdmFile };
		
		try {
			VdmToolsWrapper.generatePogFile(vppdeExecutable, vdmFiles);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileEmptyVdmFile() throws Exception {
		String vdmFile = "";
		String[] vdmFiles = new String[] { vdmFile };
		
		try {
			VdmToolsWrapper.generatePogFile(vppdeExecutable, vdmFiles);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVppdeExecutable() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			VdmToolsWrapper.generatePogFile(null, vdmFiles);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileEmptyVppdeExecutable() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			VdmToolsWrapper.generatePogFile("", vdmFiles);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileInvalidVppdeExecutable() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			VdmToolsWrapper.generatePogFile("some_inavlid_executable", vdmFiles);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}

}
