package org.overturetool.potrans.preparation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

/**
 * CommandLineToolsTest is the JUnit TestClass for the {@link CommandLineTools}
 * class.
 * 
 * @author miguel_ferreira
 *
 */
public class CommandLineToolsTest extends TestCase {
	
	private static String settingsWarning = 
		"If this test fails check that you have the correct vaules set in Settings.xml, " +
		"namelly for the test VPP models and the VDMTools binary path. ";
	private static String pogExtension = ".pog";
	
	private static String vppdeExecutable = null;
	private static String testModel1 = null;
	private static String testModel2 = null;
	
	/**
	 * Sets the initial test conditions by obtaining necessary values:
	 * <ul>
	 * <li>VDMTools command line binary path;
	 * <li>example VDM++ model path;
	 * </ul>
	 * for the execution of the test methods.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		Preferences.importPreferences(new BufferedInputStream(new FileInputStream("Settings.xml")));
		Preferences preferences = Preferences.userNodeForPackage(CommandLineTools.class);
		
		vppdeExecutable = preferences.get("vppdeExecutable", null);
		testModel1 = preferences.get("testModel1", null);
		testModel2 = preferences.get("testModel2", null);
		
		// remove previously generated files
		removePreviousTestsData();
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
	
	public void testExecuteProcessValidCommand() throws Exception {
		String inputText = "This is a test";
		String cmdText = "echo " + inputText;
		
		String output = CommandLineTools.executeProcess(cmdText);
		assertEquals(inputText, output.trim());
	}
	
	public void testExecuteProcessNullCmd() throws Exception {
		String cmdText = null;

		try {
			CommandLineTools.executeProcess(cmdText);
		} catch(Exception e) {
			assertEquals("Given a null command the method should throw a NullPointerException from Rintime.",
					NullPointerException.class, e.getClass());
		}
	}
	
	public void testExecuteProcessEmptyCmd() throws Exception {
		String cmdText = "";

		try {
			CommandLineTools.executeProcess(cmdText);
		} catch(Exception e) {
			assertEquals("Given an empty command the method should throw a IllegalArgumentException from Rintime.",
					IllegalArgumentException.class, e.getClass());
			assertEquals("Empty command", e.getMessage().trim());
		}
	}
	
	public void textExecuteProcessVDMToolsPog() throws Exception {
		String cmdText = vppdeExecutable + " -g " + testModel1;
		String expected = 
			  "Parsing \"testinput/sorter.vpp\" (Plain Text) ... done"
			+ CommandLineTools.newLine
			+ "Type checking Sorter ... done"
			+ CommandLineTools.newLine
			+ "Type checking DoSort ... done"
			+ CommandLineTools.newLine
			+ "Class Sorter with super classes are POS type correct"
			+ CommandLineTools.newLine
			+ "Generating proof obligations for DoSort...done"
			+ CommandLineTools.newLine
			+ "Generating proof obligations for Sorter...done";
		
		String output = CommandLineTools.executeProcess(cmdText);
		assertTrue(settingsWarning + "As result of the method invocation here should be a new "
				+ testModel1 + pogExtension + " file.", 
				new File(testModel2 + pogExtension).exists());
		assertEquals(settingsWarning, expected, output.trim());
	}
	
	public void testGeneratePogFile() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		String expected = 
			  "Parsing \"testinput/dosort.vpp\" (Latex) ... done"
			+ CommandLineTools.newLine
			+ "Parsing \"testinput/sorter.vpp\" (Plain Text) ... done"
			+ CommandLineTools.newLine
			+ "Type checking Sorter ... done"
			+ CommandLineTools.newLine
			+ "Type checking DoSort ... done"
			+ CommandLineTools.newLine
			+ "Class Sorter with super classes are POS type correct"
			+ CommandLineTools.newLine
			+ "Generating proof obligations for DoSort...done"
			+ CommandLineTools.newLine
			+ "Generating proof obligations for Sorter...done";

		String actual = CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		assertTrue(settingsWarning + "As result of the method invocation here should be anew  "
				+ testModel2 + pogExtension + " file.", 
				new File(testModel2 + pogExtension).exists());
		assertEquals(settingsWarning, expected, actual.trim());
	}
	
	public void testGeneratePogFileInvalidVdmFile() throws Exception {
		String vdmFile = "some_invalid_file";
		String[] vdmFiles = new String[]{ vdmFile };
		/*
		 *  TODO The VDMTools doesn't follow the OS path conventions,
		 *       and to work around this it is necessary to code a path
		 *       translation method to adjust the user.dir property,
		 *       to the VDMTools output.
		 */
		//	  "Couldn't open file '" + CommandLineTools.userDir 
		//	  + CommandLineTools.fileSeparator + vdmFile + "'"
		//	  + CommandLineTools.newLine
		String expected = "Abnormal termination with exit value <2>, and error message:";

		String actual = CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		assertTrue(settingsWarning, actual.trim().endsWith(expected));
	}
	
	public void testGeneratePogFileEmptyVdmFiles() throws Exception {
		String[] vdmFiles = new String[]{ };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVdmFiles() throws Exception {
		String[] vdmFiles = null;
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVdmFile() throws Exception {
		String vdmFile = null;
		String[] vdmFiles = new String[] { vdmFile };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileEmptyVdmFile() throws Exception {
		String vdmFile = "";
		String[] vdmFiles = new String[] { vdmFile };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVppdeExecutable() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, null);
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileEmptyVppdeExecutable() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, "");
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileInvalidVppdeExecutable() throws Exception {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, "some_inavlid_executable");
		} catch(Exception e) {
			assertEquals(settingsWarning, IllegalArgumentException.class, e.getClass());
		}
	}
}
