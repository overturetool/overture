/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.IOException;

/**
 * @author miguel_ferreira
 */
public interface CommandLineProcessInput {

	/**
	 * Depending on the kind of implementation, the output can be static or dynamic, i.e.,
	 * it can be always the same of change as invocations to the method are made. 
	 * @return bytes from the input text.
	 * @throws IOException When calls to implementations fail with <code>IOException</code>.
	 */
	public byte[] getBytes() throws IOException;
	
	/**
	 * Depending on the kind of implementation, the output can be static or dynamic, i.e.,
	 * it can be always the same of change as invocations to the method are made. 
	 * @return input text.
	 * @throws IOException When calls to implementations fail with <code>IOException</code>.
	 */
	public String getText() throws IOException;
	
	/**
	 * Test whether the input that can be read from the object is static, i.e., doesn't change.
	 * @return <code>true</code> if and only if the input text doesn't change as invocations to
	 * <code>getBytes</code> are done; <code>false</code> otherwise.
	 */
	public boolean isStatic();
}
