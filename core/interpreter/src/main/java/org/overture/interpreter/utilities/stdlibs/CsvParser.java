package org.overture.interpreter.utilities.stdlibs;

import java.util.LinkedList;
import java.util.List;

import org.overture.interpreter.values.Value;

public class CsvParser
{
	public static final String INVALID_CSV_MSG = "Invalid CSV data: cannot parse null";
	private CsvValueBuilder builder;
	
	public CsvParser(CsvValueBuilder builder)
	{
		this.builder = builder;
	}
	
	public CsvResult parseValues(String line)
	{
		List<Value> values = new LinkedList<>();
		
		if(line == null)
		{
			return new CsvResult(values, INVALID_CSV_MSG);
		}
		
		if(line.isEmpty())
		{
			return new CsvResult(values);
		}
		
		String lastError = null;
		int last = 0;
		for(int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);
			
			if(c == ',' || i == line.length() - 1)
			{
				String cell;
				
				if(c == ',')
				{
					cell = line.substring(last,i);
				}
				else
				{
					cell = line.substring(last, line.length());
				}
				
				try
				{
					Value v = builder.createValue(cell);
					values.add(v);
					last = i + 1;
				}
				catch(Exception e)
				{
					// Proceed to next comma and try to parse value again
					// Happens for values such as {1,2},{3,4}
					lastError = e.getMessage();
				}
			}
		}
		
		if(last == line.length())
		{
			return new CsvResult(values);
		}
		else
		{
			return new CsvResult(values, lastError);
		}
	}
}
