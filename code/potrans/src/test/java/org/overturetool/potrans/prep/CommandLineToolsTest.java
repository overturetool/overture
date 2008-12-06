package org.overturetool.potrans.prep;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

import org.overturetool.potrans.prep.CommandLineTools;

import junit.framework.TestCase;

/**
 * CommandLineToolsTest is the JUnit TestClass for the {@link CommandLineTools}
 * class.
 * 
 * @author Miguel Ferreira
 *
 */
public class CommandLineToolsTest extends TestCase {
	
	private String vppdeBinary = null;
	private String testModel = null;
	
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
		Preferences prefs = Preferences.userNodeForPackage(CommandLineTools.class);
		
//		System.out.println("-----------");
//		prefs.exportNode(System.out);
//		System.out.println("\n-----------");
		
		vppdeBinary = prefs.get("vppdeBinary", null);
		testModel = prefs.get("testModel", null);
	}

	/**
	 * Tests the <code>CommandLineTools.genPogFile</code> method using a valid
	 * VDM++ model.
	 */
	public void testGenPogFileSuccess() {
		try {
			int val = CommandLineTools.genPogFile(new String[]{testModel}, vppdeBinary);
			
			assertEquals(0, val);
			assertTrue(new File(testModel + ".pog").exists());
		} catch (CommandLineException e) {
			preferencesWarning();
			e.printStackTrace();
		}
	}
	
	
	private void preferencesWarning() {
		System.err.println("Test Case failed: Please check that setting vppdeBinary in Settings.xml file is pointing to a valid path.");
	}
}
