package org.overture.typechecker.tests.framework;

import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;

public class TCStruct {


	public enum Type { ERROR, WARNING};
	public Type type;
	public int number;
	public int line;
	public int column;

	public TCStruct(Type type,int number, int line, int column) {
		this.type = type;
		this.number = number;
		this.line = line;
		this.column = column;
	}

	public boolean is(VDMError error) {
		if (error.number == number
				&& error.location.startLine == line
				&& error.location.startPos == column) {
			return true;
		} else {
			return false;
		}
	}

	
	public boolean is(VDMWarning error) {
		if (error.number == number
				&& error.location.startLine == line
				&& error.location.startPos == column) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return type + ":" + number + ":" + line + "," + column;
	}
}
