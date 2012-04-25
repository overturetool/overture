package org.overturetool.parser.imp;

import java.io.File;

public class ParserError
{
	public int line;
	public int col;
	public String message;
	public File file;

	public ParserError(String message, int line, int col) {
		this.message = message;
		this.line = line;
		this.col = col;
	}
	
	@Override
	public String toString()
	{
		String fileName ="";
		if(file!=null)
			fileName = file.getName();
	return message + " in "+ fileName+ " line: "+ line+" col:"+ col;
	}
}
