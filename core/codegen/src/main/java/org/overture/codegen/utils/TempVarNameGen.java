package org.overture.codegen.utils;

import java.util.HashMap;

public class TempVarNameGen
{
	private static final int START_VALUE = 1;
	
	private HashMap<String, Integer> counters;
	
	public TempVarNameGen()
	{
		super();
		this.counters = new HashMap<String, Integer>();
	}
	
	public String nextVarName(String prefix)
	{
		int count = counters.containsKey(prefix) ? 1 + counters.get(prefix) : START_VALUE;
		
		counters.put(prefix, count);
		
		return prefix + count;
	}
}
