package org.overture.interpreter.utilities.stdlibs;

import java.util.List;

import org.overture.interpreter.values.Value;

public class CsvResult
{
	private List<Value> values;
	private String errorMsg;
	
	public CsvResult(List<Value> values)
	{
		this(values, null);
	}
	
	public CsvResult(List<Value> values, String errorMsg)
	{
		this.values = values;
		this.errorMsg = errorMsg;
	}
	
	public List<Value> getValues()
	{
		return values;
	}
	public boolean dataOk()
	{
		return errorMsg == null;
	}
	
	public String getErrorMsg()
	{
		return errorMsg;
	}
}
