package org.overture.codegen.vdm2jml.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.runtime.traces.Pair;

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
		register(newKey, stateDesVars.remove(oldKey), stateDesDecls.remove(oldKey));
	}
	
	public Pair<List<AIdentifierVarExpCG>, List<AVarDeclCG>> remove(SStmCG key)
	{
		return new Pair<List<AIdentifierVarExpCG>, List<AVarDeclCG>>(stateDesVars.remove(key), stateDesDecls.remove(key)); 
	}
	
	public void register(SStmCG key, List<AIdentifierVarExpCG> vars, List<AVarDeclCG> decls)
	{
		if(vars != null)
		{
			stateDesVars.put(key, vars);
		}

		if(decls != null)
		{
			stateDesDecls.put(key, decls);
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
					ADefaultClassDeclCG encClass = k.getAncestor(ADefaultClassDeclCG.class);
					
					if (encClass == null)
					{
						Logger.getLog().printErrorln("Could not find enclosing class of " + stateDesVar + " in '"
								+ this.getClass().getSimpleName() + "'");
					}
					
					return encClass;
				}
			}
		}
		
		return null;
	}
}
