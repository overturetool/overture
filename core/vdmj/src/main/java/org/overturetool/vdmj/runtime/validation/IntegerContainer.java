package org.overturetool.vdmj.runtime.validation;

public class IntegerContainer implements IValidationExpression {

	int i;
	
	public IntegerContainer(int i) {
		this.i = i;
	}
	
	public int getValue(){
		return i;
	}
}

