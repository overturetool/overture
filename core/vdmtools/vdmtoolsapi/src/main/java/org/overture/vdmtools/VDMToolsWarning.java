/*
 * #%~
 * VDM Tools CORBA wrapper
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.vdmtools;

public class VDMToolsWarning {
	private short lineNr;
	private short colNr;
	private String message;
	private String filename;
	
	public VDMToolsWarning(String message, String filename, short lineNr, short colNr) {
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
