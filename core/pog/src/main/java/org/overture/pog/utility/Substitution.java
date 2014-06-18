package org.overture.pog.utility;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;

/**
 * A class for holding variable substitutions to be performed at a later date by the
 * 
 * @author ldc
 */
public class Substitution
{

	String original;
	PExp newExp;
	
	

	public String getOriginal()
	{
		return original;
	}

	public Substitution(ILexNameToken var, PExp value)
	{
		this.original = makeFullKey(var);
		this.newExp = value.clone();
	}

	public Substitution(String original, PExp exp)
	{
		this.original = original;
		this.newExp = exp.clone();
	}

	public boolean containsKey(AVariableExp key)
	{
		String fullkey = makeFullKey(key.getName());
		return original.equals(fullkey);
	}

	public PExp get(AVariableExp key)
	{
		String fullkey = makeFullKey(key.getName());
		if (original.equals(fullkey))
		{
			return newExp;
		}
		return null;

	}

	public String makeFullKey(ILexNameToken name)
	{
		return name.getName() + (name.getOld() ? "$OLD" : "");
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(original);
		sb.append("/");
		sb.append(newExp.toString());
		sb.append("]");
		return sb.toString();
	}

}
