package org.overturetool.vdmtools.dbgp;

public class Breakpoint {
	
	/**
	 * 
	 * line		 	filename, lineNumber 	break on the given lineNumber in the given file
	 * call 		function 				break on entry into new stack for function name
     * return 		function 				break on exit from stack for function name
	 * exception 	exception 				break on exception of the given name
	 * conditional 	expression, filename 	break when the given expression is true at the given filename and line number or just in given filename
	 * watch 		expression 				break on write of the variable or address defined by the expression argument
	 *
	 */
//	public static enum BreakpointType {LINE, FILE, CALL, RETURN, EXCEPTION, CONDITIONAL, WATCH};
	
	private int id;
	private boolean enabled;
	private String filename;
	private int lineNumber;
	private DBGPBreakpointType type;
	private int hitValue;
	private int hitCondition;
	private int currentHitCount;
	private String expression = "";
	private boolean isTemporary;

	public boolean isTemporary() {
		return isTemporary;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public DBGPBreakpointType getType() {
		return type;
	}

	public Breakpoint(String filename, int linenumber, boolean enabled, DBGPBreakpointType type, String hitCondition, String expression, int hitValue, boolean isTemp) {
		this.filename = filename;
		this.lineNumber = linenumber;
		this.enabled = enabled;
		this.type = type;
		if (!expression.equals("")){
			this.expression = expression;
		}
		this.isTemporary = isTemp;
		setHitCondition(hitCondition);
		this.hitValue = hitValue;
		currentHitCount = 0;
	}
	
	public int getCurrentHitCount() {
		return currentHitCount;
	}

	public String getHitCondition() {
		if (hitCondition == 0)
		{
			return "";
		}
		if (hitCondition == 1)
			return ">=";
		if (hitCondition == 2)
			return "==";
		if (hitCondition == 3)
			return "%";
		return "==";
	}
	
	public void setHitCondition(String hitCondition) {
		if (!hitCondition.equals("")) {
			if (hitCondition.equals(">=")) {
				this.hitCondition = 1;
			}
			if (hitCondition.equals("==")) {
				this.hitCondition = 2;
			}
			if (hitCondition.equals("%")) {
				this.hitCondition = 3;
			}
		}
		else
		{
			this.hitCondition = 0;
		}
	}

	public int getId() {
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getFilename() {
		return filename;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getState()
	{
		if (isEnabled()){
			return "enabled";
		}else
		{
			return "disabled";
		}
	}

	public int getHitValue() {
		return hitValue;
	}

	public void setHitValue(int hitValue) {
		this.hitValue = hitValue;
	}

}
