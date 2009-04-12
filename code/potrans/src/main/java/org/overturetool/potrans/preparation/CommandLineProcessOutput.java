/**
 * 
 */
package org.overturetool.potrans.preparation;

/**
 * @author miguel_ferreira
 *
 */
public class CommandLineProcessOutput {
	
	protected StringBuffer outputBuffer = new StringBuffer();
	
	public void appendOutput(String output) {
		outputBuffer.append(output);
	}
	
	public void appendErrorTrace(Exception e) {
		outputBuffer.append(e.getStackTrace());
	}
	
	public String getOutput() {
		return outputBuffer.toString();
	}
}
