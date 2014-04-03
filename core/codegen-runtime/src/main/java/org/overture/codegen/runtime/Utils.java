package org.overture.codegen.runtime;

public class Utils
{
	public static int hashCode(Object... fields)
	{
		if(fields == null)
			throw new IllegalArgumentException("Fields cannot be null");

		int hashcode = 0;
		
		for(int i = 0; i < fields.length; i++)
		{
			Object currentField = fields[i];
			hashcode += currentField != null ? currentField.hashCode() : 0;
		}
		
		return hashcode;
	}
	
	public static int index(Number value)
	{
		if(value.longValue() < 1)
			throw new IllegalArgumentException("VDM subscripts must be >= 1");
		
		return toInt(value) - 1;
	}
	
	private static int toInt(Number value) {
		
		long valueLong = value.longValue();
		
	    if (valueLong < Integer.MIN_VALUE || valueLong > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (valueLong + " Casting long will change its value.");
	    }
	    return (int) valueLong;
	}
		
	public static String recordToString(Record record, Object... fields)
	{
		if(record == null)
			throw new IllegalArgumentException("Record cannot be null in recordToString");
		
		if(fields == null)
			throw new IllegalArgumentException("Fields cannot be null in recordToString");
		
		StringBuilder str = new StringBuilder();

		str.append(fields[0]);

		for (int i = 1; i < fields.length; i++)
		{
			str.append(", " + fields[i]);
		}

		return "mk_" + record.getClass().getSimpleName() + "(" + str + ")";
	}
}
