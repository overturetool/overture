package org.overture.tools.astcreator.parser;

import java.io.File;

public interface IError
{
	/**
	 * get the file of which the error occurred
	 * @return the file where the error occurred
	 */
	File getFile();

	/**
	 * Get the line of which the error was discovered
	 * @return the line of the error
	 */
	int getLine();

	/**
	 * Get the char position in the line where the error was discovered
	 * @return char pos in line
	 */
	int getCharPositionInLine();
	
	/**
	 * Get a description of the error encountered by the parser
	 * @return the message
	 */
	String getMessage();
}
