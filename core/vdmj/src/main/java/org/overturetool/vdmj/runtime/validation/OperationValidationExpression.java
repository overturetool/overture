package org.overturetool.vdmj.runtime.validation;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.rtlog.RTMessage.MessageType;

public class OperationValidationExpression implements IValidationExpression {

	
	private String className;
	private String opName;
	private MessageType type;
	
	
	//TODO: The names should probably be changed to LexNameToken
	public OperationValidationExpression(String opName, String className, MessageType type) {
		this.className = className;
		this.opName = opName;
		this.type = type;
	}
	
	public boolean evaluate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		
		switch (type) {
		case Activate:
			s.append("#act");
			break;
		case Completed:
			s.append("#fin");
			break;
		case Request:
			s.append("#req");
			break;
		default:
			break;
		}
		s.append("(");
		s.append(className); s.append("`"); s.append(opName);
		s.append(")");
		
		return s.toString();
	}

	

	public boolean isAssociatedWith(String opname, String classdef) {
		return opname.equals(this.opName) && classdef.equals(this.className);
	}

	public boolean matches(String opname, String classname, MessageType kind) {
		return opname.equals(this.opName) && classname.equals(this.className) && kind.equals(this.type);
	}
	
	
}
