package org.overturetool.umltrans.Main;

import java.util.List;

import org.overturetool.parser.imp.ParserError;

public class ParseException extends Exception
{
	List<ParserError> errors;
	String message;

	public ParseException(String message, List<ParserError> errors) {
		this.message = message;
		this.errors = errors;
	}
	
	public List<ParserError> getErrors()
	{
		return errors;
	}
	public String getMessage()
	{
		return message;
	}

	@Override
	public String toString()
	{
		String info = message + ":";
		for (Object o : errors)
		{
			message += "\n" + o.toString();
		}
//		message += "\n";
//		message += super.toString();
		return message;
	}
}
