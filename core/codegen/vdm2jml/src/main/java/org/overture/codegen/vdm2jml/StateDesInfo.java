package org.overture.codegen.vdm2jml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;

public class StateDesInfo
{
	private Map<SStmCG, List<AIdentifierVarExpCG>> stateDesVars;
	
	public StateDesInfo()
	{
		this.stateDesVars = new HashMap<SStmCG, List<AIdentifierVarExpCG>>();
	}
	
	public void addStateDesVars(SStmCG stm, List<AIdentifierVarExpCG> stateDesVars)
	{
		this.stateDesVars.put(stm, stateDesVars);
	}
	
	public List<AIdentifierVarExpCG> getStateDesVars(SStmCG stm)
	{
		return stateDesVars.get(stm);
	}
}
