package org.overture.typechecker.tests.framework;

import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public class TCStruct {


	
	public int number;
	public int line;
	public int column;

	public TCStruct(int number, int line, int column) {
		
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
		return number + ":" + line + "," + column;
	}
}
