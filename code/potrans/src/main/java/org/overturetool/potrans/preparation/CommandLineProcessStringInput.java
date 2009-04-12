/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.IOException;

/**
 * @author miguel_ferreira
 *
 */
public class CommandLineProcessStringInput implements CommandLineProcessInput {

	private final String input;
	
	public CommandLineProcessStringInput(String input) {
		this.input = input;
	}
	
	/**
	 * This implementation's input is static.
	 * @return the bytes form the input text. All the invocations return the same bytes.
	 */
	public byte[] getBytes() {
		return input.getBytes();
	}

	/*
	 * (non-Javadoc)
	 * @see org.overturetool.potrans.preparation.CommandLineProcessInput#isStatic()
	 */
	public boolean isStatic() {
		return true;
	}

	public String getText() throws IOException {
		return new String(input);
	}

}
