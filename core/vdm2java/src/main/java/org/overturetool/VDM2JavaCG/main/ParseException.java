package org.overturetool.VDM2JavaCG.main;

import java.util.List;

import org.overturetool.vdmj.debug.*;

public class ParseException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<DBGPErrorCode> errors;
	String message;

	public ParseException(String message, List<DBGPErrorCode> errors) {
		this.message = message;
		this.errors = errors;
	}
	
	public List<DBGPErrorCode> getErrors()
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
		@SuppressWarnings("unused")
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
