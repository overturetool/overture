/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.StringReader;

import org.overturetool.potrans.external_tools.CommandLineProcessReaderInput;

import junit.framework.TestCase;

/**
 * @author gentux
 * 
 */
public class CommandLineProcessReaderInputTest extends TestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.CommandLineProcessReaderInput#CommandLineProcessReaderInput(java.io.Reader)}
	 * .
	 */
	public void testCommandLineProcessReaderInput() throws Exception {
		String expected = "test string";
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(expected));

		String actual = readerInput.buffer.readLine();

		assertEquals(expected, actual);
		assertNull(readerInput.getText());
	}
	
	public void testCommandLineProcessReaderInputNullInput() throws Exception {
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				null);

		assertNull(readerInput.buffer);
	}
	
	public void testCommandLineProcessReaderInputEmptyInput() throws Exception {
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(""));

		assertNotNull(readerInput.buffer);
		assertNull(readerInput.buffer.readLine());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.CommandLineProcessReaderInput#getBytes()}
	 * .
	 */
	public void testGetBytes() throws Exception {
		String testInput = "test string";
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(testInput));
		byte[] expected = testInput.getBytes();
		byte[] actual = readerInput.getBytes();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}

	public void testGetBytesEmptyInput() throws Exception {
		String emptyString = "";
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(emptyString));
		byte[] actual = readerInput.getBytes();

		assertNull(actual);
	}
	
	public void testGetBytesNullInput() throws Exception {
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				null);
		byte[] actual = readerInput.getBytes();

		assertNull(actual);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.CommandLineProcessReaderInput#isStatic()}
	 * .
	 */
	public void testIsStatic() {
		String expected = "test string";
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(expected));

		assertFalse(readerInput.isStatic());
	}
	
	public void testIsStaticEmptyInput() {
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(""));

		assertFalse(readerInput.isStatic());
	}

	public void testIsStaticNullInput() {
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				null);

		assertFalse(readerInput.isStatic());
	}
	
	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.CommandLineProcessReaderInput#getText()}
	 * .
	 */
	public void testGetText() throws Exception {
		String expected = "test string";
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(expected));

		String actual = readerInput.getText();

		assertEquals(expected, actual);
	}
	
	public void testGetTextEmptyInput() throws Exception {
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				new StringReader(""));

		assertNull(readerInput.getText());
	}
	
	public void testGetTextNullInput() throws Exception {
		CommandLineProcessReaderInput readerInput = new CommandLineProcessReaderInput(
				null);

		assertNull(readerInput.getText());
	}

}
