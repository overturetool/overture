package org.overture.pog.visitors;

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

	public Substitution(ILexNameToken var, PExp value)
	{
		this.original = var.getFullName();
		this.newExp = value.clone();
	}

	public Substitution(String original, PExp exp)
	{
		this.original = original;
		this.newExp = exp.clone();
	}

	public boolean containsKey(AVariableExp key)
	{
		return original.equals(key.getName().getFullName());
	}

	public PExp get(AVariableExp key)
	{
		if (original.equals(key.getName().getFullName()))
		{
			return newExp;
		}
		return null;

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
