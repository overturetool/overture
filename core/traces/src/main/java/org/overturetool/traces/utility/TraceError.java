package org.overturetool.traces.utility;

import java.io.File;

 public class TraceError {
	public String ClassName;
	public String Trace;
	public String Message;
	public int Line;
	public int Column;
	public File File;

	public TraceError(File file, String className, String traceName, String message, int line, int col) {
		this.File = file;
		this.ClassName = className;
		this.Trace = traceName;
		this.Message = message;
		this.Line = line;
		this.Column = col;
	}

	public String toString() {
		return ClassName + " " + Trace + " (l " + Line + ", c " + Column
				+ ") " + Message;
	}
}