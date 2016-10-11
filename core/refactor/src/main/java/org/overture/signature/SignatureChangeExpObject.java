package org.overture.signature;

import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;

public class SignatureChangeExpObject {
	private PType type;
	private PExp expression;
	
	public SignatureChangeExpObject(){
	}
	
	public PType getType() {
		return type;
	}
	
	public void setType(PType type) {
		this.type = type;
	}
	
	public PExp getExpression() {
		return expression;
	}
	
	public void setExpression(PExp expression) {
		this.expression = expression;
	}
}
