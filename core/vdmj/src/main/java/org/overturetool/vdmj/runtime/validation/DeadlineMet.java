package org.overturetool.vdmj.runtime.validation;

public class DeadlineMet extends ConjectureDefinition {

	public DeadlineMet(String name,OperationValidationExpression opExpr,
			ValueValidationExpression valueExpr,
			IValidationExpression endingExpr, int interval) {
		super(name,opExpr, valueExpr, endingExpr, interval);
		startupValue = false;
	}
	
	@Override
	public boolean validate(long triggerTime, long endTime) {
		return endTime - triggerTime <= this.interval;
	}


	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("deadlineMet(");
		s.append(super.toString());
		s.append(")");
		return s.toString();
	}
	
}
