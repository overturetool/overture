package org.overture.codegen.vdm2jml.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.runtime.traces.Pair;

public class StateDesInfo
{
	private Map<SStmIR, List<AIdentifierVarExpIR>> stateDesVars;
	private Map<SStmIR, List<AVarDeclIR>> stateDesDecls;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public StateDesInfo()
	{
		this.stateDesVars = new HashMap<>();
		this.stateDesDecls = new HashMap<>();
	}
	
	public void addStateDesVars(SStmIR stm, List<AIdentifierVarExpIR> stateDesVars)
	{
		this.stateDesVars.put(stm, stateDesVars);
	}
	
	public void addStateDesDecl(SStmIR stm, List<AVarDeclIR> stateDesDecls)
	{
		this.stateDesDecls.put(stm, stateDesDecls);
	}
	
	public void replaceStateDesOwner(SStmIR oldKey, SStmIR newKey)
	{
		register(newKey, stateDesVars.remove(oldKey), stateDesDecls.remove(oldKey));
	}
	
	public Pair<List<AIdentifierVarExpIR>, List<AVarDeclIR>> remove(SStmIR key)
	{
		return new Pair<List<AIdentifierVarExpIR>, List<AVarDeclIR>>(stateDesVars.remove(key), stateDesDecls.remove(key)); 
	}
	
	public void register(SStmIR key, List<AIdentifierVarExpIR> vars, List<AVarDeclIR> decls)
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
	
	public boolean isStateDesDecl(AVarDeclIR decl)
	{
		for (SStmIR stm : stateDesDecls.keySet())
		{
			List<AVarDeclIR> decls = stateDesDecls.get(stm);
			
			if(decls != null)
			{
				for (AVarDeclIR d : decls)
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
	
	public List<AIdentifierVarExpIR> getStateDesVars(SStmIR stm)
	{
		return stateDesVars.get(stm);
	}
	
	public ADefaultClassDeclIR getEnclosingClass(AIdentifierVarExpIR stateDesVar)
	{
		for(SStmIR k : stateDesVars.keySet())
		{
			for(AIdentifierVarExpIR v : stateDesVars.get(k))
			{
				if(v == stateDesVar)
				{
					ADefaultClassDeclIR encClass = k.getAncestor(ADefaultClassDeclIR.class);
					
					if (encClass == null)
					{
						log.error("Could not find enclosing class of "
								+ stateDesVar);
					}
					
					return encClass;
				}
			}
		}
		
		return null;
	}
}
