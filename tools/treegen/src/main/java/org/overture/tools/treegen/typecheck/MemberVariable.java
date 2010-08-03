package org.overture.tools.treegen.typecheck;

public class MemberVariable {

	// keep track of the type of the member variable
	public Type type;
	
	// keep track of the initialization code
	public String init;
	
	// constructor
	public MemberVariable(Type ptp, String pi) { type = ptp; init = pi; }
	
}
