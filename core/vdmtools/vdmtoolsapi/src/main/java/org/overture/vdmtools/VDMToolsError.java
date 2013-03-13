package org.overture.vdmtools;

public class VDMToolsError {
	private short lineNr;
	private short colNr;
	private String message;
	private String filename;
	
	public VDMToolsError(String message, String filename, short lineNr, short colNr) {
		this.lineNr = lineNr;
		this.colNr = colNr;
		this.message = message;
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public String getMessage() {
		return message;
	}

	public short getColNr() {
		return colNr;
	}

	public short getLineNr() {
		return lineNr;
	}
}
