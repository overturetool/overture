package org.overturetool.proofsupport.external_tools.hol;

public abstract class HolInterpreterFactory {

	public static HolInterpreter newHolInterepterInstance(HolParameters holParam) throws HolInterpreterException {
		return new HolInterpreterImpl(holParam);
	}
	
}
