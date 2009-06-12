package org.overturetool.potrans.external_tools.hol;

import org.overturetool.potrans.external_tools.SystemProperties;
import org.overturetool.potrans.external_tools.hol.HolEnvironmentBuilder;
import org.overturetool.potrans.external_tools.hol.MosmlHolConsole;

import junit.framework.TestCase;

public class MosmlHolConsoleTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testQuit() throws Exception {
		HolParameters holParam = new HolParameters("/Users/gentux/root/opt/mosml", "/Users/gentux/root/opt/hol");
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
		
		assertEquals(0, returnValue);
	}
	
	public void testInputOutputQuit() throws Exception {
		HolEnvironmentBuilder holEnv = new HolEnvironmentBuilder("/Users/gentux/root/opt/mosml", "lib", "bin");
		HolParameters holParam = new HolParameters("/Users/gentux/root/opt/mosml", "/Users/gentux/root/opt/hol");
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
		mosml.writeLine("help;");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		assertEquals(0, returnValue);
	}
	
	public void testRemoveConsoleHeader() throws Exception {
		HolEnvironmentBuilder holEnv = new HolEnvironmentBuilder("/Users/gentux/root/opt/mosml", "lib", "bin");
		HolParameters holParam = new HolParameters("/Users/gentux/root/opt/mosml", "/Users/gentux/root/opt/hol");
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
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
		HolParameters holParam = new HolParameters("/Users/gentux/root/opt/mosml", "/Users/gentux/root/opt/hol");
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
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
		HolParameters holParam = new HolParameters("/Users/gentux/root/opt/mosml", "/Users/gentux/root/opt/hol");
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());		
		mosml.removeConsoleHeader();
		mosml.writeLine(invalidId + ";");
		String actual = mosml.readOutputBlock();
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
			
		assertEquals(0, returnValue);
		assertEquals(expected, actual);
	}
}
