package org.overture.ct.ctruntime.tests.util;

import java.util.List;
import java.util.Vector;

public class TraceResult
{
	public String traceName;
	public List<TraceTest> tests = new Vector<TraceTest>();
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Trace name: " + traceName).append("\n");
		
		for(TraceTest test : tests)
		{
			sb.append(test).append("\n");
		}
		
		return sb.toString();
	}
	
	@Override
	public int hashCode()
	{
		int hashCode = 0;
		
		hashCode += traceName != null ? traceName.hashCode() : 0;
		
		for(TraceTest test : tests)
		{
			hashCode += test.hashCode();
		}
		
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
		{
			return false;
		}
		
		if(getClass() != obj.getClass())
		{
			return false;
		}
		
		final TraceResult other = (TraceResult) obj;
		
		if(this.traceName == null ? other.traceName != null : !this.traceName.equals(other.traceName))
		{
			return false;
		}

		return tests == null ? other.tests == null : tests.equals(other.tests);
	}
}