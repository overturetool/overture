package org.overturetool.vdmj.runtime.validation;


public class Separate extends ConjectureDefinition {

	public Separate(String name,OperationValidationExpression opExpr,
			ValueValidationExpression valueExpr,
			IValidationExpression endingExpr, int interval) {
		super(name,opExpr, valueExpr, endingExpr, interval);
		startupValue = true;		
	}

	@Override
	public boolean validate(long triggerTime, long endTime) {
		return endTime - triggerTime >= this.interval;
	}

	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("separate(");
		s.append(super.toString());
		s.append(")");
		return s.toString();
	}


}
