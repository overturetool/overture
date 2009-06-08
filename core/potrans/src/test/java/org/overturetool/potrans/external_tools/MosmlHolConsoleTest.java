package org.overturetool.potrans.external_tools;

import junit.framework.TestCase;

public class MosmlHolConsoleTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testQuit() throws Exception {
		MosmlHolConsole mosml = new MosmlHolConsole("/Users/gentux/root/opt/mosml/bin/mosml", "/Users/gentux/root/opt/hol");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
		
		assertEquals(0, returnValue);
	}
	
	public void testInputOutputQuit() throws Exception {
		HolEnvironmentBuilder holEnv = new HolEnvironmentBuilder("/Users/gentux/root/opt/mosml", "lib", "bin");
		MosmlHolConsole mosml = new MosmlHolConsole("/Users/gentux/root/opt/mosml/bin/mosml", "/Users/gentux/root/opt/hol", holEnv.getEnvironment());
		mosml.writeLine("help;");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		assertEquals(0, returnValue);
	}
	
	public void testRemoveConsoleHeader() throws Exception {
		HolEnvironmentBuilder holEnv = new HolEnvironmentBuilder("/Users/gentux/root/opt/mosml", "lib", "bin");
		MosmlHolConsole mosml = new MosmlHolConsole("/Users/gentux/root/opt/mosml/bin/mosml", "/Users/gentux/root/opt/hol", holEnv.getEnvironment());
		mosml.removeConsoleHeader();
		mosml.writeLine("help;");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
		
		StringBuffer sb = new StringBuffer();
		String line = "";
		while((line = mosml.readLine()) != null) {
			sb.append(line);
			if(line != null && !line.equals("- "))
				sb.append(SystemProperties.LINE_SEPARATOR);
		}
		
		assertEquals(0, returnValue);
		assertEquals("> val it = fn : string -> unit\n- ", sb.toString());
	}
	
	public void testReadOutputOneLineBlock() throws Exception {
		HolEnvironmentBuilder holEnv = new HolEnvironmentBuilder("/Users/gentux/root/opt/mosml", "lib", "bin");
		MosmlHolConsole mosml = new MosmlHolConsole("/Users/gentux/root/opt/mosml/bin/mosml", "/Users/gentux/root/opt/hol", holEnv.getEnvironment());
		mosml.removeConsoleHeader();
		mosml.writeLine("help;");
		String actual = mosml.readOutputBlock();
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
		
		assertEquals(0, returnValue);
		assertEquals("> val it = fn : string -> unit", actual);
	}
	
	public void testReadOutputMultiLineBlock() throws Exception {
		String invalidId = "invalidId";
		String expected = 
			"! Toplevel input:\n" +
			"! " + invalidId + ";\n" +
			"! ^^^^^^^^^\n" +
			"! Unbound value identifier: " + invalidId;
		HolEnvironmentBuilder holEnv = new HolEnvironmentBuilder("/Users/gentux/root/opt/mosml", "lib", "bin");
		MosmlHolConsole mosml = new MosmlHolConsole("/Users/gentux/root/opt/mosml/bin/mosml", "/Users/gentux/root/opt/hol", holEnv.getEnvironment());
		mosml.removeConsoleHeader();
		mosml.writeLine(invalidId + ";");
		String actual = mosml.readOutputBlock();
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
			
		assertEquals(0, returnValue);
		assertEquals(expected, actual);
	}
}
