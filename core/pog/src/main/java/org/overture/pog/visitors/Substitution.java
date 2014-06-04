package org.overture.pog.visitors;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;

/**
 * A class for holding variable substitutions to be performed at a later date
 * by the 
 * 
 * @author ldc
 * 
 */
public class Substitution {

	String var;
	PExp value;

	public Substitution(AVariableExp var, PExp value) {
		super();
		this.var = var.getName().getName();
		this.value = value;
	}

	public Substitution(ILexNameToken var, PExp value) {
		super();
		this.var = var.getName();
		this.value = value.clone();
	}
	

	public boolean containsKey(AVariableExp key) {
		return var.equals(key.getName().getName());
	}

	public PExp get(AVariableExp key) {
		if (var.equals(key.getName().getName())) {
			return value;
		}
		return null;

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(var);
		sb.append("/");
		sb.append(value.toString());
		sb.append("]");
		return sb.toString();
	}
	
	

}
