/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.StringReader;

import org.overturetool.potrans.external_tools.ConsoleProcessInputReader;

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
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcessInputReader#CommandLineProcessReaderInput(java.io.Reader)}
	 * .
	 */
	public void testCommandLineProcessReaderInput() throws Exception {
		String expected = "test string";
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(expected));

		String actual = readerInput.buffer.readLine();

		assertEquals(expected, actual);
		assertNull(readerInput.getText());
	}
	
	public void testCommandLineProcessReaderInputNullInput() throws Exception {
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				null);

		assertNull(readerInput.buffer);
	}
	
	public void testCommandLineProcessReaderInputEmptyInput() throws Exception {
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(""));

		assertNotNull(readerInput.buffer);
		assertNull(readerInput.buffer.readLine());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcessInputReader#getBytes()}
	 * .
	 */
	public void testGetBytes() throws Exception {
		String testInput = "test string";
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(testInput));
		byte[] expected = testInput.getBytes();
		byte[] actual = readerInput.getBytes();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}

	public void testGetBytesEmptyInput() throws Exception {
		String emptyString = "";
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(emptyString));
		byte[] actual = readerInput.getBytes();

		assertNull(actual);
	}
	
	public void testGetBytesNullInput() throws Exception {
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				null);
		byte[] actual = readerInput.getBytes();

		assertNull(actual);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcessInputReader#isStatic()}
	 * .
	 */
	public void testIsStatic() {
		String expected = "test string";
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(expected));

		assertFalse(readerInput.isStatic());
	}
	
	public void testIsStaticEmptyInput() {
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(""));

		assertFalse(readerInput.isStatic());
	}

	public void testIsStaticNullInput() {
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				null);

		assertFalse(readerInput.isStatic());
	}
	
	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcessInputReader#getText()}
	 * .
	 */
	public void testGetText() throws Exception {
		String expected = "test string";
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(expected));

		String actual = readerInput.getText();

		assertEquals(expected, actual);
	}
	
	public void testGetTextEmptyInput() throws Exception {
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				new StringReader(""));

		assertNull(readerInput.getText());
	}
	
	public void testGetTextNullInput() throws Exception {
		ConsoleProcessInputReader readerInput = new ConsoleProcessInputReader(
				null);

		assertNull(readerInput.getText());
	}

}
