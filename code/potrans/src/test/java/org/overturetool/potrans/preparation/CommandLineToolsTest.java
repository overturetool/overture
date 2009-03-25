package org.overturetool.potrans.preparation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

/**
 * CommandLineToolsTest is the JUnit TestClass for the {@link CommandLineTools}
 * class.
 * 
 * @author Miguel Ferreira
 *
 */
public class CommandLineToolsTest extends TestCase {
	
	private static String pogExtension = ".pog";
	
	private String vppdeExecutable = null;
	private String testModel1 = null;
	private String testModel2 = null;
	
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
	private void removePreviousTestsData() {
		File testModel1Pog = new File(testModel1 + pogExtension);
		File testModel2Pog = new File(testModel2 + pogExtension);
		
		if(testModel1Pog.exists()) {
			testModel1Pog.delete();
		}
		
		if(testModel2Pog.exists()) {
			testModel2Pog.delete();
		}
	}
	
	public void testExecuteProcessValidCommand() {
		String inputText = "This is a test";
		String cmdText = "echo " + inputText;
		
		String output = CommandLineTools.executeProcess(cmdText);
		assertEquals(inputText, output.trim());
	}
	
	public void testExecuteProcessNullCmd() {
		String cmdText = null;

		try {
			CommandLineTools.executeProcess(cmdText);
		} catch(Exception e) {
			assertEquals("Given a null command the method should throw a NullPointerException from Rintime.",
					NullPointerException.class, e.getClass());
		}
	}
	
	public void testExecuteProcessEmptyCmd() {
		String cmdText = "";

		try {
			CommandLineTools.executeProcess(cmdText);
		} catch(Exception e) {
			assertEquals("Given an empty command the method should throw a IllegalArgumentException from Rintime.",
					IllegalArgumentException.class, e.getClass());
			assertEquals("Empty command", e.getMessage().trim());
		}
	}
	
	public void textExecuteProcessVDMToolsPog() {
		String cmdText = vppdeExecutable + " -g " + testModel1;
		String expected = 
			  "Parsing \"testinput/sorter.vpp\" (Latex) ... done"
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
		assertTrue("As result of the method invocation here should be a new "
				+ testModel1 + pogExtension + " file.", 
				new File(testModel2 + pogExtension).exists());
		assertEquals(expected, output.trim());
	}
	
	public void testGeneratePogFile() {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		String expected = 
			  "Parsing \"testinput/dosort.vpp\" (Latex) ... done"
			+ CommandLineTools.newLine
			+ "Parsing \"testinput/sorter.vpp\" (Latex) ... done"
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
		assertTrue("As result of the method invocation here should be anew  "
				+ testModel2 + pogExtension + " file.", 
				new File(testModel2 + pogExtension).exists());
		assertEquals(expected, actual.trim());
	}
	
	public void testGeneratePogFileInvalidVdmFile() {
		String vdmFile = "some_invalid_file";
		String[] vdmFiles = new String[]{ vdmFile };
		String expected = 
			  "Couldn't open file '" + CommandLineTools.userDir 
			  + CommandLineTools.fileSeparator + vdmFile + "'"
			  + CommandLineTools.newLine
			  + "Abnormal termination with exit value <2>, and error message:";

		String actual = CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		assertEquals(expected, actual.trim());
	}
	
	public void testGeneratePogFileEmptyVdmFiles() {
		String[] vdmFiles = new String[]{ };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVdmFiles() {
		String[] vdmFiles = null;
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVdmFile() {
		String vdmFile = null;
		String[] vdmFiles = new String[] { vdmFile };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileEmptyVdmFile() {
		String vdmFile = "";
		String[] vdmFiles = new String[] { vdmFile };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, vppdeExecutable);
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileNullVppdeExecutable() {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, null);
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileEmptyVppdeExecutable() {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, "");
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	public void testGeneratePogFileInvalidVppdeExecutable() {
		String[] vdmFiles = new String[]{ testModel2, testModel1 };
		
		try {
			CommandLineTools.generatePogFile(vdmFiles, "some_inavlid_executable");
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
}
