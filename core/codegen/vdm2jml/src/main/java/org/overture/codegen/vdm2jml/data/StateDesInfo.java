package org.overture.codegen.vdm2jml.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;

public class StateDesInfo
{
	private Map<SStmCG, List<AIdentifierVarExpCG>> stateDesVars;
	private Map<SStmCG, List<AVarDeclCG>> stateDesDecls;
	
	public StateDesInfo()
	{
		this.stateDesVars = new HashMap<>();
		this.stateDesDecls = new HashMap<>();
	}
	
	public void addStateDesVars(SStmCG stm, List<AIdentifierVarExpCG> stateDesVars)
	{
		this.stateDesVars.put(stm, stateDesVars);
	}
	
	public void addStateDesDecl(SStmCG stm, List<AVarDeclCG> stateDesDecls)
	{
		this.stateDesDecls.put(stm, stateDesDecls);
	}
	
	public void replaceStateDesOwner(SStmCG oldKey, SStmCG newKey)
	{
		List<AIdentifierVarExpCG> vars = stateDesVars.remove(oldKey);
		
		if(vars != null)
		{
			stateDesVars.put(newKey, vars);
		}
		
		List<AVarDeclCG> decls = stateDesDecls.remove(oldKey);
		
		if(decls != null)
		{
			stateDesDecls.put(newKey, decls);
		}
	}
	
	public boolean isStateDesDecl(AVarDeclCG decl)
	{
		for (SStmCG stm : stateDesDecls.keySet())
		{
			List<AVarDeclCG> decls = stateDesDecls.get(stm);
			
			if(decls != null)
			{
				for (AVarDeclCG d : decls)
				{
					if (d == decl)
					{
						return true;
					}
				}
			}

		}

		return false;
	}
	
	public List<AIdentifierVarExpCG> getStateDesVars(SStmCG stm)
	{
		return stateDesVars.get(stm);
	}
	
	public ADefaultClassDeclCG getEnclosingClass(AIdentifierVarExpCG stateDesVar)
	{
		for(SStmCG k : stateDesVars.keySet())
		{
			for(AIdentifierVarExpCG v : stateDesVars.get(k))
			{
				if(v == stateDesVar)
				{
					return k.getAncestor(ADefaultClassDeclCG.class);
				}
			}
		}
		
		return null;
	}
}
