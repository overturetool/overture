/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author miguel_ferreira
 */
public class CommandLineProcessReaderInput implements CommandLineProcessInput {

	protected BufferedReader buffer = null;
	
	public CommandLineProcessReaderInput(Reader input) {
		if(input != null)
			buffer = new BufferedReader(input);
	}
	
	/**
	 * This implementation's input is dynamic.
	 * @return the bytes form the next line of the input text. Returns <code>null</code> 
	 * if there is no more input text. 
	 */
	public byte[] getBytes() throws IOException {
		String line = getText();
		return (line != null) ? line.getBytes() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overturetool.potrans.preparation.CommandLineProcessInput#isStatic()
	 */
	public boolean isStatic() {
		return false;
	}

	public String getText() throws IOException {
		return (buffer != null) ? buffer.readLine() : null;
	}
}
