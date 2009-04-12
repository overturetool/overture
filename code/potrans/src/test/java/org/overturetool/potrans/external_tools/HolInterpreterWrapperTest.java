/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.overturetool.potrans.external_tools.InputValidator;

import junit.framework.TestCase;

/**
 * @author miguel_ferreira
 *
 */
public class HolInterpreterWrapperTest extends TestCase {

	private final static String newLine = System.getProperty("line.separator");

	private final static String HOL_INTERPRETER_JVM_PROERTY = "-DholExecutable=<path to executable>";
	
	private final static String HOL_INTERPRETER_PROPERTY_ERROR_MESSAGE = 
		"You have to set the flag " + HOL_INTERPRETER_JVM_PROERTY + " for the JVM in"
			+ " the JUnit launch configuration.";

	private final static String settingsWarning = 
		"If this test fails check that you have the correct vaules set in Settings.xml, " +
		"namelly for the test HOL models and tactics. Also check that you set the property " +
		"\"" + HOL_INTERPRETER_JVM_PROERTY + "\" in the JVM arguments.";
	
	private final static String holExecutable = System.getProperty("holExecutable");
	
	private static String testTheory1 = null;
	private static String vdmHolTactics = null;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		setUpPreferences();
		InputValidator.validateStringNotEmptyNorNull(holExecutable,
				HOL_INTERPRETER_PROPERTY_ERROR_MESSAGE);
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
		testTheory1 = preferences.get("testTheory1", null);
		vdmHolTactics = preferences.get("vdmHolTactics", null);
	}

	/**
	 * Test method for {@link org.overturetool.potrans.external_tools.HolInterpreterWrapper#interpretTheory(java.lang.String, java.lang.String)}.
	 */
//	public void testInterpretTheory() throws Exception {
//		String actual = HolInterpreterWrapper.interpretTheory(holExecutable, vdmHolTactics, testTheory1);
//		
//		System.out.println(actual);
//		
//	}
	
	public void testInterpretTheory() throws Exception {
//		String actual = HolInterpreterWrapper.interpretTheory(holExecutable, "/Users/gentux/empty");
//		
//		System.out.println(actual);
//		
	}

}
