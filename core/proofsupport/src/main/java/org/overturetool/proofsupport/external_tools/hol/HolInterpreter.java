package org.overturetool.proofsupport.external_tools.hol;

public interface HolInterpreter {

	public void start();
	
	public void quit() throws HolInterpreterException;
	
	public String interpretLine(String line) throws HolInterpreterException;
	
	public String[] interpretModel(String holCode) throws HolInterpreterException;
	
	public boolean dischargeProof(String proofCommand) throws HolInterpreterException;
	
}
